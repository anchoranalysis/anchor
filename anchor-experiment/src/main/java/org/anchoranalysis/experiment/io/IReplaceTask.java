/* (C)2020 */
package org.anchoranalysis.experiment.io;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * If an experiment implements this interface, the task of an an experiment can be replaced by
 * another
 *
 * @param <T> input-object type
 * @param <S> shared-state for job
 * @author Owen Feehan
 */
public interface IReplaceTask<T extends InputFromManager, S> {

    public void replaceTask(Task<T, S> taskToReplace) throws OperationFailedException;
}
