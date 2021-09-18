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

package org.anchoranalysis.experiment.bean.task;

import java.util.List;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.NoSharedState;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.inference.concurrency.ConcurrencyPlan;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;

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
            Outputter outputter,
            ConcurrencyPlan concurrencyPlan,
            List<T> inputs,
            ParametersExperiment params)
            throws ExperimentExecutionException {
        // No shared-state by default, so we use a placeholder shared-state type
        return NoSharedState.INSTANCE;
    }

    @Override
    public final void afterAllJobsAreExecuted(NoSharedState sharedState, InputOutputContext context)
            throws ExperimentExecutionException {
        // NOTHING TO DO BY DEFAULT. This method exists so it can be overridden with
        // custom-behaviour.
    }
}
