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
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.bean.log.ToConsole;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.generator.combined.ManifestGenerator;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.PathPrefixerException;
import org.anchoranalysis.io.output.recorded.MultiLevelRecordedOutputs;
import org.anchoranalysis.io.output.recorded.RecordedOutputsWithRules;
import org.apache.commons.lang.time.StopWatch;

/**
 * An experiment that uses a {@link OutputManager} to specify the outputting of results.
 *
 * @author Owen Feehan
 */
public abstract class OutputExperiment extends Experiment {

    public static final String OUTPUT_NAME_MANIFEST = "experiment_manifest";
    
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
    public void executeExperiment(ExperimentExecutionArguments arguments)
            throws ExperimentExecutionException {

        try {
            doExperimentWithParams(createParams(arguments));

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
     * @param params a combination of run-time and bean-time specified elements used in the
     *     experiment.
     * @throws ExperimentExecutionException if anything occurs stop the experiment finishing its
     *     execution
     */
    protected abstract void executeExperimentWithParams(ParametersExperiment params)
            throws ExperimentExecutionException;

    /**
     * If specified, default rules for determine which outputs are enabled or not.
     *
     * @return the default rules if they exist.
     */
    protected abstract MultiLevelOutputEnabled defaultOutputs();

    private void doExperimentWithParams(ParametersExperiment params)
            throws ExperimentExecutionException {
        try {
            StopWatch stopWatchExperiment = new StopWatch();
            stopWatchExperiment.start();

            initBeforeExecution(params);
            executeExperimentWithParams(params);
            tidyUpAfterExecution(params, stopWatchExperiment);
        } finally {
            // An experiment is considered always successful
            params.getLoggerExperiment().close(true);
        }
    }

    private ParametersExperiment createParams(ExperimentExecutionArguments arguments)
            throws CreateException {

        Manifest experimentalManifest = new Manifest();

        String experimentId = experimentIdentifier.identifier(arguments.getTaskName());

        try {
            OutputterChecked rootOutputter =
                    getOutput()
                            .createExperimentOutputter(
                                    experimentId,
                                    experimentalManifest,
                                    new RecordedOutputsWithRules(
                                            recordedOutputs,
                                            defaultOutputs(),
                                            arguments.getOutputEnabledDelta()),
                                    arguments.createPrefixerContext());

            Preconditions.checkArgument(rootOutputter.getSettings().hasBeenInitialized());

            // Important we bind to a root folder before any log messages go out, as
            // certain log appenders require the OutputManager to be set before outputting
            // to the correct location and this only occurs after the call to
            // bindRootFolder()
            return new ParametersExperiment(
                    arguments,
                    experimentId,
                    Optional.of(experimentalManifest),
                    rootOutputter,
                    getOutput().getFilePathPrefixer(),
                    createLogger(rootOutputter, arguments),
                    useDetailedLogging());
        } catch (PathPrefixerException e) {
            throw new CreateException("Cannot create params-context", e);
        } catch (BindFailedException e) {
            throw new CreateException("Bind failed", e);
        }
    }

    private StatefulMessageLogger createLogger(
            OutputterChecked rootOutputter, ExperimentExecutionArguments expArgs) {
        return logExperiment.createWithConsoleFallback(
                rootOutputter, expArgs, useDetailedLogging());
    }

    private void initBeforeExecution(ParametersExperiment params)
            throws ExperimentExecutionException {
        params.getLoggerExperiment().start();
        OutputExperimentLogHelper.maybeLogStart(params);

        if (!params.getOutputter().getChecked().getSettings().hasBeenInitialized()) {
            throw new ExperimentExecutionException("Experiment has not been initialized");
        }
    }

    private void tidyUpAfterExecution(ParametersExperiment params, StopWatch stopWatchExperiment) {
        
        params.getExperimentalManifest().ifPresent( manifest ->
            params.getOutputter().writerSelective().write(OUTPUT_NAME_MANIFEST, ManifestGenerator::new, () -> manifest)
        );
        
        stopWatchExperiment.stop();

        OutputExperimentLogHelper.maybeLogCompleted(recordedOutputs, params, stopWatchExperiment);
    }
}
