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

package org.anchoranalysis.experiment.bean.processor;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.io.ReplaceTask;
import org.anchoranalysis.experiment.log.Divider;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Processes a job
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public abstract class JobProcessor<T extends InputFromManager, S>
        extends AnchorBean<JobProcessor<T, S>> implements ReplaceTask<T, S> {

    private static final Divider DIVIDER = new Divider();

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Task<T, S> task;

    @BeanField @Getter @Setter private boolean suppressExceptions = true;
    // END BEAN PROPERTIES

    /**
     * Executes the tasks, gathers statistics, and logs them
     *
     * @param rootOutputter
     * @param inputs
     * @param paramsExperiment
     * @return task statistics
     * @throws ExperimentExecutionException
     */
    public TaskStatistics executeLogStats(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException {

        if (paramsExperiment.isDetailedLogging()) {
            paramsExperiment.getLoggerExperiment().log(DIVIDER.withLabel("Processing"));
        }

        TaskStatistics stats = execute(rootOutputter, inputs, paramsExperiment);

        if (paramsExperiment.isDetailedLogging()) {
            logStats(stats, paramsExperiment);
        }

        return stats;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void replaceTask(Task<T, S> taskToReplace) throws OperationFailedException {

        // This is a bit hacky. If the underlying task inherit from IReplaceTask then, rather than
        // directly
        //  replacing the task, we call this method. In effect, this allows skipping of the task
        // that is replaced.
        if (ReplaceTask.class.isAssignableFrom(this.task.getClass())) {
            ((ReplaceTask<T, S>) this.task).replaceTask(taskToReplace);
        } else {
            // If not, then we replace the task directly
            this.task = taskToReplace;
        }
    }

    /** Is an input-object compatible with this particular task? */
    public boolean isInputCompatibleWith(Class<? extends InputFromManager> inputClass) {
        return task.isInputCompatibleWith(inputClass);
    }

    /** Is the execution-time of the task per-input expected to be very quick to execute? */
    public boolean hasVeryQuickPerInputExecution() {
        return task.hasVeryQuickPerInputExecution();
    }

    /**
     * The job processor is expected to remove items from the inputs-list as they are consumed so as
     * to allow garbage-collection of these items before all jobs are processed (as the list might
     * be quite large).
     *
     * @param rootOutputter
     * @param inputs
     * @param paramsExperiment
     * @return
     * @throws ExperimentExecutionException
     */
    protected abstract TaskStatistics execute(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException;

    protected Optional<MessageLogger> loggerForMonitor(ParametersExperiment paramsExperiment) {
        return OptionalUtilities.createFromFlag(
                paramsExperiment.isDetailedLogging(), paramsExperiment::getLoggerExperiment);
    }

    private static void logStats(TaskStatistics stats, ParametersExperiment paramsExperiment) {
        StatisticsLogger statisticsLogger =
                new StatisticsLogger(paramsExperiment.getLoggerExperiment());
        statisticsLogger.logTextualMessage(stats);
    }
}
