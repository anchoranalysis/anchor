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

package org.anchoranalysis.experiment.task.processor;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.task.ErrorReporterForTask;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * A job derived from a {@link Task} that can be placed on different threads
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-object bean
 */
public class CallableJob<T extends InputFromManager, S>
        implements Callable<Optional<JobExecutionException>> {

    private Task<T, S> task;
    private ParametersUnbound<T, S> parametersUnbound;
    private JobStateMonitor stateMonitor;
    private JobDescription jobDescription;
    private JobStartStopLogger logger;

    /**
     * Constructor.
     *
     * @param task the task that will be executed.
     * @param parametersUnbound parameters for the task, that have yet to be bound to a job.
     * @param stateMonitor monitors the state of a job.
     * @param jobDescription a unique description of the job.
     * @param monitor monitors state changes across jobs.
     * @param loggerMonitor the logger used for the monitor.
     * @param showOngoingJobsLessThan When the number of ongoing jobs is less than this threshold,
     *     they are shown in event logs. 0 disables.
     */
    public CallableJob(
            Task<T, S> task,
            ParametersUnbound<T, S> parametersUnbound,
            JobStateMonitor stateMonitor,
            JobDescription jobDescription,
            ConcurrentJobMonitor monitor,
            Optional<MessageLogger> loggerMonitor,
            int showOngoingJobsLessThan) {
        super();
        this.task = task;
        this.parametersUnbound = parametersUnbound;
        this.stateMonitor = stateMonitor;
        this.jobDescription = jobDescription;
        this.logger =
                new JobStartStopLogger(
                        "Job", monitor, false, showOngoingJobsLessThan, loggerMonitor);
    }

    @Override
    public Optional<JobExecutionException> call() {

        try {
            Task<T, S> taskDuplicated = task.duplicateBean();

            stateMonitor.markAsExecuting();

            logger.logStart(jobDescription);

            boolean success = taskDuplicated.executeJob(parametersUnbound);

            closeJobStateAndLog(success);

            return Optional.empty();

        } catch (Throwable e) { // NOSONAR
            // Note that throwable is needed here instead of Exception, so that Errors don't
            // cause errors in our job monitoring.

            // If executeTask is called with suppressException==true then exceptions shouldn't occur
            // here as a rule from specific-tasks,
            //   as they should be logged internally to task-log. So if any error is actually thrown
            // here, let's consider it suspiciously
            //
            // If executeTask is called with suppressException==false then we arrive here fairly
            // easily, and record the error in the experiment-log just
            //  in case, even though it's probably already in the task log.
            ErrorReporter errorReporter =
                    new ErrorReporterForTask(
                            parametersUnbound.getParametersExperiment().getLoggerExperiment());
            errorReporter.recordError(CallableJob.class, e);

            closeJobStateAndLog(false);

            return Optional.of(new JobExecutionException(e));
        } finally {
            Preconditions.checkArgument(!stateMonitor.isExecuting());
        }
    }

    private void closeJobStateAndLog(boolean success) {
        stateMonitor.markAsCompleted(success);
        logger.logEnd(jobDescription, stateMonitor);
    }
}
