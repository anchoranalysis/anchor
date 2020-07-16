/* (C)2020 */
package org.anchoranalysis.experiment.bean.task;

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.NoSharedState;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * A particular type of task that doesn't share-state between running jobs
 *
 * <p>N.B. this is an important differentiation when it comes to parallelization
 *
 * <p>Sharing-state between running jobs is only possible when they are run as different threads in
 * the same VM.
 *
 * <p>If the different jobs are processes on different VMs (e.g. on different cloud instances) this
 * task (and its subclasses) should work without problems. For the tasks with shared-state, they
 * will break.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 */
public abstract class TaskWithoutSharedState<T extends InputFromManager>
        extends Task<T, NoSharedState> {

    @Override
    public final NoSharedState beforeAnyJobIsExecuted(
            BoundOutputManagerRouteErrors outputManager, ParametersExperiment params)
            throws ExperimentExecutionException {
        // No shared-state by default, so we use a placeholder shared-state type
        return NoSharedState.INSTANCE;
    }

    @Override
    public final void afterAllJobsAreExecuted(NoSharedState sharedState, BoundIOContext context)
            throws ExperimentExecutionException {
        // NOTHING TO DO BY DEFAULT. This method exists so it can be overridden with
        // custom-behaviour.
    }
}
