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
    private ParametersUnbound<T, S> paramsUnbound;
    private JobState jobState;
    private JobDescription jobDescription;
    private JobStartStopLogger logger;

    /**
     * Constructor
     *
     * @param task
     * @param paramsUnbound
     * @param taskState
     * @param jobDescription
     * @param monitor
     * @param loggerMonitor the logger used for the monitor
     */
    public CallableJob(
            Task<T, S> task,
            ParametersUnbound<T, S> paramsUnbound,
            JobState taskState,
            JobDescription jobDescription,
            ConcurrentJobMonitor monitor,
            Optional<MessageLogger> loggerMonitor,
            int showOngoingJobsLessThan) {
        super();
        this.task = task;
        this.paramsUnbound = paramsUnbound;
        this.jobState = taskState;
        this.jobDescription = jobDescription;
        this.logger =
                new JobStartStopLogger(
                        "Job", monitor, false, showOngoingJobsLessThan, loggerMonitor);
    }

    @Override
    public Optional<JobExecutionException> call() {

        try {
            Task<T, S> taskDup = task.duplicateBean();

            jobState.markAsExecuting();

            logger.logStart(jobDescription);

            boolean success = taskDup.executeJob(paramsUnbound);

            closeJobStateAndLog(success);

            return Optional.empty();

        } catch (Throwable e) { // NOSONAR
            // Note that throwable is needed here instead of Exception, so that Errors don't
            // cause errors in our job monitoring.

            // If executeTask is called with suppressException==true then exceptions shouldn't occur
            // here as a rule from specific-tasks,
            //   as they should be logged internally to task-log. So if any error is actually thrown
            // here, let's consider it suspciously
            //
            // If executeTask is called with suppressException==false then we arrive here fairly
            // easily, and record the error in the experiment-log just
            //  in case, even though it's probably already in the task log.
            ErrorReporter errorReporter =
                    new ErrorReporterForTask(
                            paramsUnbound.getParametersExperiment().getLoggerExperiment());
            errorReporter.recordError(CallableJob.class, e);

            closeJobStateAndLog(false);

            return Optional.of(new JobExecutionException(e));
        } finally {
            Preconditions.checkArgument(!jobState.isExecuting());
        }
    }

    private void closeJobStateAndLog(boolean success) {
        jobState.markAsCompleted(success);
        logger.logEnd(jobDescription, jobState, success);
    }
}
