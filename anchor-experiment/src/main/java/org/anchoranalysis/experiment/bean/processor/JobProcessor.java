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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Divider;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.io.ReplaceTask;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Base class for a method to apply a task on inputs.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public abstract class JobProcessor<T extends InputFromManager, S>
        extends AnchorBean<JobProcessor<T, S>> implements ReplaceTask<T, S> {

    private static final Divider DIVIDER = new Divider();

    // START BEAN PROPERTIES
    /** The task to be applied to inputs. */
    @BeanField @Getter @Setter private Task<T, S> task;

    /**
     * When true, any exception thrown processing and input is caught and logged.
     *
     * <p>This stops processing the affected input, but otherwise allows processing of other inputs
     * to continue.
     *
     * <p>When false, an exception throw for a single input is immediately thrown, so all processing
     * stops.
     */
    @BeanField @Getter @Setter private boolean suppressExceptions = true;

    // END BEAN PROPERTIES

    /**
     * Executes the task on all {@code inputs} and logs statistics about this to the file-systen.
     *
     * @param rootOutputter an outputter, bound to the base (root) output directory into which
     *     outputed files are written.
     * @param inputs the inputs to apply {@code task} on.
     * @param parametersExperiment parameters that exist pertaining to the experiment that is
     *     underway.
     * @return statistics about the success/failure/execution-time etc. of applying the task to
     *     inputs.
     * @throws ExperimentExecutionException if anything goes wrong executing the experiment (but not
     *     necessarily if a processing a particular input fails when {@code suppressExceptions ==
     *     true}.
     */
    public TaskStatistics executeLogStatistics(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment parametersExperiment)
            throws ExperimentExecutionException {

        if (parametersExperiment.isDetailedLogging()) {
            parametersExperiment.getLoggerExperiment().log(DIVIDER.withLabel("Processing"));
        }

        TaskStatistics statistics = execute(rootOutputter, inputs, parametersExperiment);

        if (parametersExperiment.isDetailedLogging()) {
            logStatistics(statistics, parametersExperiment);
        }

        return statistics;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void replaceTask(Task<T, S> taskToReplace) throws OperationFailedException {

        // This is a bit hacky. If the underlying task inherit from ReplaceTask then, rather than
        // directly replacing the task, we call this method.
        // In effect, this allows skipping of the task that is replaced.
        if (ReplaceTask.class.isAssignableFrom(this.task.getClass())) {
            ((ReplaceTask<T, S>) this.task).replaceTask(taskToReplace);
        } else {
            // If not, then we replace the task directly
            this.task = taskToReplace;
        }
    }

    /**
     * Is an input-type compatible with this particular task?
     *
     * @param inputClass a class describing the type of input, that is checked for compatibility.
     * @return true iff the input-type is compatible.
     */
    public boolean isInputCompatibleWith(Class<? extends InputFromManager> inputClass) {
        return task.isInputCompatibleWith(inputClass);
    }

    /**
     * Is the execution-time of the task per-input expected to be very quick to execute?
     *
     * @return true when the execution-time is expected to be very quick, or false otherwise.
     */
    public boolean hasVeryQuickPerInputExecution() {
        return task.hasVeryQuickPerInputExecution();
    }

    /**
     * Executes the task on all {@code inputs}.
     *
     * <p>It is expected that elements are removed from {@code inputs} as they are consumed so as to
     * allow garbage-collection of these items before all jobs are processed (as the list might be
     * quite large).
     *
     * @param rootOutputter an outputter, bound to the base (root) output directory into which
     *     outputted files are written.
     * @param inputs the inputs to apply {@code task} on.
     * @param parametersExperiment parameters that exist pertaining to the experiment that is
     *     underway.
     * @return statistics about the success/failure/execution-time etc. of applying the task to
     *     inputs.
     * @throws ExperimentExecutionException if anything goes wrong executing the experiment (but not
     *     necessarily if a processing a particular input fails when {@code suppressExceptions ==
     *     true}.
     */
    protected abstract TaskStatistics execute(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment parametersExperiment)
            throws ExperimentExecutionException;

    private static void logStatistics(
            TaskStatistics stats, ParametersExperiment parametersExperiment) {
        StatisticsLogger statisticsLogger =
                new StatisticsLogger(parametersExperiment.getLoggerExperiment());
        statisticsLogger.logStatisticsDescription(stats);
    }
}
