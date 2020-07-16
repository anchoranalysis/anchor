/* (C)2020 */
package org.anchoranalysis.experiment.bean.io;

import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.bean.log.ToConsole;
import org.anchoranalysis.experiment.bean.processor.JobProcessor;
import org.anchoranalysis.experiment.io.IReplaceInputManager;
import org.anchoranalysis.experiment.io.IReplaceOutputManager;
import org.anchoranalysis.experiment.io.IReplaceTask;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bean.OutputManager;

/**
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state for job
 */
public class InputOutputExperiment<T extends InputFromManager, S> extends OutputExperiment
        implements IReplaceInputManager, IReplaceOutputManager, IReplaceTask<T, S> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private InputManager<T> input;

    @BeanField @Getter @Setter private JobProcessor<T, S> taskProcessor;

    @BeanField @Getter @Setter private LoggingDestination logTask = new ToConsole();
    // END BEAN PROPERTIES

    @Override
    protected void execExperiment(ParametersExperiment params) throws ExperimentExecutionException {

        try {
            List<T> inputObjects =
                    getInput()
                            .inputObjects(
                                    new InputManagerParams(
                                            params.getExperimentArguments().createInputContext(),
                                            ProgressReporterNull.get(),
                                            new Logger(params.getLoggerExperiment())));
            checkCompabilityInputObjects(inputObjects);

            params.setLoggerTaskCreator(logTask);

            taskProcessor.executeLogStats(params.getOutputManager(), inputObjects, params);

        } catch (AnchorIOException | IOException e) {
            throw new ExperimentExecutionException(
                    "An error occured while searching for inputs", e);
        }
    }

    @Override
    public boolean useDetailedLogging() {

        // Disable detailed-logging if the task has a very quick execution (unless we are in 'force'
        // mode)
        if (isForceDetailedLogging() || !taskProcessor.hasVeryQuickPerInputExecution()) {
            return true;
        }

        return super.useDetailedLogging();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void replaceInputManager(InputManager<?> inputManager) throws OperationFailedException {
        this.input = (InputManager<T>) inputManager;
    }

    @Override
    public void replaceOutputManager(OutputManager outputManager) throws OperationFailedException {
        this.setOutput(outputManager);
    }

    @Override
    public void replaceTask(Task<T, S> taskToReplace) throws OperationFailedException {
        this.taskProcessor.replaceTask(taskToReplace);
    }

    private void checkCompabilityInputObjects(List<T> inputObjects)
            throws ExperimentExecutionException {
        for (T obj : inputObjects) {
            if (!taskProcessor.isInputObjectCompatibleWith(obj.getClass())) {
                throw new ExperimentExecutionException(
                        String.format(
                                "Input has an incompatible class for the associated task: %s",
                                obj.getClass().toString()));
            }
        }
    }
}
