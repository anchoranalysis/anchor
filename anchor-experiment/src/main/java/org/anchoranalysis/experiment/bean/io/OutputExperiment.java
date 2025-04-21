/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.experiment.bean.io;

import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.bean.log.ToConsole;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.ExperimentFeedbackContext;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.time.DescribeExecutionTimeStatistics;
import org.anchoranalysis.experiment.time.ExecutionTimeRecorderFactory;
import org.anchoranalysis.io.generator.text.StringGenerator;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputWriteContext;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;
import org.anchoranalysis.io.output.recorded.MultiLevelRecordedOutputs;
import org.anchoranalysis.io.output.recorded.RecordedOutputsWithRules;
import org.apache.commons.lang.time.StopWatch;

/**
 * An {@link Experiment} that produces output files, as defined by a {@link OutputManager}.
 *
 * @author Owen Feehan
 */
public abstract class OutputExperiment extends Experiment {

    /** The name for the output that records the total execution-time of the experiment. */
    public static final String OUTPUT_EXECUTION_TIME = "executionTime";

    // START BEAN PROPERTIES
    /** The output-manager that specifies how/where/which elements occur duing outputting. */
    @BeanField @Getter @Setter private OutputManager output;

    /**
     * Where log messages that <b>do not</b> pertain to a specific job (input) appear.
     *
     * <p>Note that in the case of a {@link InputOutputExperiment} an additional log will be created
     * for each specific job.
     */
    @BeanField @Getter @Setter private LoggingDestination logExperiment = new ToConsole();

    /** A name for the experiment. */
    @BeanField @Getter @Setter private ExperimentIdentifier experimentIdentifier;

    /**
     * Whether more detailed log-messages around each experiment occur.
     *
     * <p>if true, then detailed messages around each experiment (name, time, start-stop events
     * etc.) are ALWAYS displayed if false, these will sometimes be hidden (e.g. if the execution of
     * each file is very quick)
     */
    @BeanField @Getter @Setter private boolean forceDetailedLogging = false;

    // END BEAN PROPERTIES

    /**
     * If defined, records output-names that are written / not-written during the experiment.
     *
     * <p>This only occurs for first-level outputs, not second-level outputs.
     */
    private MultiLevelRecordedOutputs recordedOutputs = new MultiLevelRecordedOutputs();

    /**
     * Executes the experiment for given arguments.
     *
     * @param arguments additional run-time configuration/parameters that influences the experiment.
     */
    @Override
    public Optional<Path> executeExperiment(ExecutionArguments arguments)
            throws ExperimentExecutionException {

        try {
            ParametersExperiment parameters = createParameters(arguments);
            doExperimentWithParameters(parameters);
            return Optional.of(parameters.getOutputter().getOutputDirectory());

        } catch (CreateException e) {
            throw new ExperimentExecutionException(e);
        }
    }

    @Override
    public boolean useDetailedLogging() {
        return forceDetailedLogging;
    }

    /**
     * Executes the experiment for parameters.
     *
     * @param parameters a combination of run-time and bean-time specified elements used in the
     *     experiment.
     * @return statistics of the tasks, if they exist.
     * @throws ExperimentExecutionException if anything occurs stop the experiment finishing its
     *     execution
     */
    protected abstract Optional<TaskStatistics> executeExperimentWithParameters(
            ParametersExperiment parameters) throws ExperimentExecutionException;

    /**
     * If specified, default rules for determine which outputs are enabled or not.
     *
     * @return the default rules if they exist.
     */
    protected abstract MultiLevelOutputEnabled defaultOutputs();

    private void doExperimentWithParameters(ParametersExperiment parameters)
            throws ExperimentExecutionException {
        try {
            StopWatch stopWatchExperiment = new StopWatch();
            stopWatchExperiment.start();

            initBeforeExecution(parameters);
            Optional<TaskStatistics> taskStatistics = executeExperimentWithParameters(parameters);
            tidyUpAfterExecution(parameters, stopWatchExperiment, taskStatistics);
        } finally {
            // An experiment is considered always successful
            parameters.getLoggerExperiment().close(true, false);
        }
    }

    private ParametersExperiment createParameters(ExecutionArguments arguments)
            throws CreateException {

        String experimentId = experimentIdentifierOrOmit(arguments);
        Optional<String> experimentIdentifierForOutputPath =
                OptionalFactory.create(
                        !arguments.output().isOmitExperimentIdentifier(), () -> experimentId);

        MultiLevelOutputEnabled outputsUnrecorded = defaultOutputs();
        RecordedOutputsWithRules outputs =
                new RecordedOutputsWithRules(
                        recordedOutputs,
                        outputsUnrecorded,
                        arguments.output().getOutputEnabledDelta());
        Optional<ImageFileFormat> suggestedImageOutputFormat =
                arguments.output().getPrefixer().getSuggestedImageOutputFormat();
        try {
            MultiLevelOutputEnabled enabledOutputs = getOutput().determineEnabledOutputs(outputs);

            ExecutionTimeRecorder executionTimeRecorder =
                    ExecutionTimeRecorderFactory.create(
                            enabledOutputs.isOutputEnabled(OUTPUT_EXECUTION_TIME));

            OutputWriteContext writeContext =
                    getOutput()
                            .createContextForWriting(
                                    suggestedImageOutputFormat, executionTimeRecorder);

            OutputterChecked rootOutputter =
                    getOutput()
                            .createExperimentOutputter(
                                    experimentIdentifierForOutputPath,
                                    enabledOutputs,
                                    outputs.getRecordedOutputs(),
                                    writeContext,
                                    arguments.derivePathPrefixerContext(),
                                    arguments.input().getCallUponDirectoryCreation(),
                                    Optional.empty());

            Preconditions.checkArgument(rootOutputter.getSettings().hasBeenInitialized());

            ExperimentFeedbackContext feedbackContext =
                    new ExperimentFeedbackContext(
                            createLogger(rootOutputter, arguments),
                            useDetailedLogging(),
                            executionTimeRecorder);
            return new ParametersExperiment(
                    arguments,
                    experimentId,
                    rootOutputter,
                    getOutput().getPrefixer(),
                    feedbackContext);
        } catch (PathPrefixerException e) {
            throw new CreateException("Cannot create parameters-context", e);
        } catch (BindFailedException e) {
            throw new CreateException("Bind failed", e);
        }
    }

    private String experimentIdentifierOrOmit(ExecutionArguments arguments) {
        return experimentIdentifier.identifier(arguments.task().getTaskName());
    }

    private StatefulMessageLogger createLogger(
            OutputterChecked rootOutputter, ExecutionArguments executionArguments) {
        return logExperiment.createWithConsoleFallback(
                rootOutputter, executionArguments, useDetailedLogging());
    }

    /** Starts the experiment-logger, and checks the output-manager has been initialzied. */
    private void initBeforeExecution(ParametersExperiment parameters)
            throws ExperimentExecutionException {
        try {
            parameters.getLoggerExperiment().start();
        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(e);
        }
        OutputExperimentLogHelper.maybeLogStart(parameters);

        if (!parameters.getOutputter().getChecked().getSettings().hasBeenInitialized()) {
            throw new ExperimentExecutionException("Experiment has not been initialized");
        }
    }

    private void tidyUpAfterExecution(
            ParametersExperiment parameters,
            StopWatch stopWatchExperiment,
            Optional<TaskStatistics> taskStatistics) {

        stopWatchExperiment.stop();

        long totalExecutionTimeSeconds = stopWatchExperiment.getTime() / 1000;

        if (taskStatistics.isPresent()) {
            parameters
                    .getOutputter()
                    .writerSelective()
                    .write(
                            OUTPUT_EXECUTION_TIME,
                            StringGenerator::new,
                            () ->
                                    DescribeExecutionTimeStatistics.describeExecutionTimes(
                                            taskStatistics.get().executionTimeTotal(),
                                            parameters.executionTimeStatistics(),
                                            totalExecutionTimeSeconds));
        }

        OutputExperimentLogHelper.maybeRecordedOutputs(recordedOutputs, parameters);

        OutputExperimentLogHelper.maybeLogCompleted(parameters, totalExecutionTimeSeconds);
    }
}
