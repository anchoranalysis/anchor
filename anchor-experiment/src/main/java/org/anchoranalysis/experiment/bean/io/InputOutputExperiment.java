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

import com.owenfeehan.pathpatternfinder.PathPatternFinder;
import com.owenfeehan.pathpatternfinder.Pattern;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.log.Divider;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.core.value.LanguageUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.bean.log.ToConsole;
import org.anchoranalysis.experiment.bean.processor.JobProcessor;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.io.ReplaceInputManager;
import org.anchoranalysis.experiment.io.ReplaceOutputManager;
import org.anchoranalysis.experiment.io.ReplaceTask;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.InputsWithDirectory;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.file.NamedFile;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.apache.commons.io.IOCase;

/**
 * An experiment that uses both an {@link InputManager} to specify inputs and a {@link
 * OutputManager} to specify outputting.
 *
 * <p>The following outputs are produced:
 *
 * <table>
 * <caption></caption>
 * <thead>
 * <tr><th>Output Name</th><th>Default?</th><th>Description</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>{@value InputOutputExperiment#OUTPUT_EXPERIMENT_LOG}</td><td>yes</td><td>A textual log file for non-job-specific messages, one for the entire experiment.</td></tr>
 * <tr><td>{@value InputOutputExperiment#OUTPUT_JOB_LOG}</td><td>yes</td><td>A textual log file for job-specific messages, one for each job.</td></tr>
 * </tbody>
 * </table>
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state for job
 */
public class InputOutputExperiment<T extends InputFromManager, S> extends OutputExperiment
        implements ReplaceInputManager<T>, ReplaceOutputManager, ReplaceTask<T, S> {

    private static final String EXECUTION_TIME_COLLECTING_INPUTS = "Collecting inputs";

    private static final String OUTPUT_EXPERIMENT_LOG = "logExperiment";

    private static final String OUTPUT_JOB_LOG = "logJob";

    private static final Divider DIVIDER = new Divider();

    // START BEAN PROPERTIES
    /** The input-manager to specify where/which/how necessary inputs for the experiment occur. */
    @BeanField @Getter @Setter private InputManager<T> input;

    /**
     * What task is associated with the experiment, and how it is processed.
     *
     * <p>e.g. how the task processes the inputs in the form of jobs (sequentially, parallel, how
     * many processors? etc.)
     */
    @BeanField @Getter @Setter private JobProcessor<T, S> taskProcessor;

    /**
     * Where log messages that <b>do</b> pertain to a specific job (input) appear.
     *
     * <p>This is in contrast to {@code logExperiment} where non-job specific log messages appear.
     */
    @BeanField @Getter @Setter private LoggingDestination logTask = new ToConsole();

    /**
     * A message written to the logger if no inputs exist for the experiment, and it thus ends
     * early.
     */
    @BeanField @Getter @Setter private String messageNoInputs = "No inputs exist. Nothing to do.";

    // END BEAN PROPERTIES

    @Override
    public boolean useDetailedLogging() {
        // Disable detailed-logging if the task has a very quick execution
        // (unless we are in 'force' mode).
        if (isForceDetailedLogging() || !taskProcessor.hasVeryQuickPerInputExecution()) {
            return true;
        }

        return super.useDetailedLogging();
    }

    @Override
    public void replaceInputManager(InputManager<T> inputManager) throws OperationFailedException {
        this.input = inputManager;
    }

    @Override
    public void replaceOutputManager(OutputManager output) throws OperationFailedException {
        this.setOutput(output);
    }

    @Override
    public void replaceTask(Task<T, S> taskToReplace) throws OperationFailedException {
        this.taskProcessor.replaceTask(taskToReplace);
    }

    @Override
    protected Optional<TaskStatistics> executeExperimentWithParameters(
            ParametersExperiment parameters) throws ExperimentExecutionException {
        try {
            OperationContext operationContext =
                    new OperationContext(
                            parameters.getExecutionTimeRecorder(),
                            new Logger(parameters.getLoggerExperiment()));
            InputManagerParameters parametersInput =
                    new InputManagerParameters(
                            parameters.getExecutionArguments().inputContextParameters(),
                            operationContext);

            if (parameters.isDetailedLogging()) {
                parameters.getLoggerExperiment().log(DIVIDER.withLabel("Inputs"));
            }

            InputsWithDirectory<T> inputs =
                    parameters
                            .getExecutionTimeRecorder()
                            .recordExecutionTime(
                                    EXECUTION_TIME_COLLECTING_INPUTS,
                                    () -> getInput().inputs(parametersInput));
            checkCompabilityInputs(inputs.inputs());

            if (!inputs.isEmpty()) {
                return Optional.of(executeExperimentWithInputs(inputs, parameters));
            } else {
                parameters.getLoggerExperiment().log(messageNoInputs);
                parameters.getLoggerExperiment().logEmptyLine();
                return Optional.empty();
            }

        } catch (InputReadFailedException e) {
            throw new ExperimentExecutionException(
                    "An error occured while searching for inputs", e);
        }
    }

    @Override
    protected MultiLevelOutputEnabled defaultOutputs() {
        OutputEnabledMutable taskDefaultOutputs = taskProcessor.getTask().defaultOutputs();
        taskDefaultOutputs.addEnabledOutputFirst(OUTPUT_EXPERIMENT_LOG, OUTPUT_JOB_LOG);
        return taskDefaultOutputs;
    }

    private TaskStatistics executeExperimentWithInputs(
            InputsWithDirectory<T> inputs, ParametersExperiment parameters)
            throws ExperimentExecutionException {
        parameters.setLoggerTaskCreator(logTask);

        if (parameters.isDetailedLogging()) {
            describeInputs(parameters.getLoggerExperiment(), inputs.inputs());
        }

        Optional<Collection<NamedFile>> nonInputs = CopyNonInputs.prepare(inputs, parameters);

        TaskStatistics statistics =
                taskProcessor.executeLogStatistics(
                        parameters.getOutputter(), inputs.inputs(), parameters);

        if (nonInputs.isPresent()) {
            CopyNonInputs.copy(nonInputs.get(), parameters);
        }

        return statistics;
    }

    private void checkCompabilityInputs(List<T> listInputs) throws ExperimentExecutionException {
        for (T inputObject : listInputs) {
            if (!taskProcessor.isInputCompatibleWith(inputObject.getClass())) {
                throw new ExperimentExecutionException(
                        String.format(
                                "Input has an incompatible class for the associated task: %s",
                                inputObject.getClass().toString()));
            }
        }
    }

    /**
     * Writes a message to the log describing the number of inputs, and any pattern in their naming.
     */
    private void describeInputs(MessageLogger log, List<T> inputs) {
        List<Path> identifiers =
                FunctionalList.mapToList(inputs, InputFromManager::identifierAsPath);
        Pattern pattern = PathPatternFinder.findPatternPaths(identifiers, IOCase.SYSTEM, true);

        // Replace any backslashes with forward slashes, as conventionally we use only forward
        // slashes in the input identifiers.
        String patternDescription = pattern.describeDetailed().replace("\\", "/");

        int numberInputs = inputs.size();
        log.logFormatted(
                "The job has %d %s.",
                numberInputs, LanguageUtilities.pluralizeMaybe(numberInputs, "input"));
        if (numberInputs > 1) {
            log.logEmptyLine();
            log.logFormatted("They are named with the pattern: %s", patternDescription);
        }
    }
}
