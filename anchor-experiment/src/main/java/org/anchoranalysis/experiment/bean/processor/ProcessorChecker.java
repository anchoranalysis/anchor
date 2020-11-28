package org.anchoranalysis.experiment.bean.processor;

import java.util.List;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Common checks for classes that inherit from {@link JobProcessor}. */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class ProcessorChecker {

    /**
     * Checks that at least one input exists for a task, otherwise throwing an exception.
     * 
     * @param <T> type in {@code inputs}
     * @param inputs the inputs for the task
     * @throws ExperimentExecutionException if {@code inputs} has no items
     */
    public static <T> void checkAtLeastOneInput(List<T> inputs) throws ExperimentExecutionException {
        if (!inputs.isEmpty()) {
            throw new ExperimentExecutionException("This task has no inputs. Nothing to do.");
        }
    }
}
