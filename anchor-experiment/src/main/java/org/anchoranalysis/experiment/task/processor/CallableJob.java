/* (C)2020 */
package org.anchoranalysis.experiment.task.processor;

import java.util.Optional;
import java.util.concurrent.Callable;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.Task;
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
     * @param taskDescription
     * @param monitor
     * @param loggerMonitor the logger used for the monitor
     */
    public CallableJob(
            Task<T, S> task,
            ParametersUnbound<T, S> paramsUnbound,
            JobState taskState,
            JobDescription taskDescription,
            ConcurrentJobMonitor monitor,
            Optional<MessageLogger> loggerMonitor,
            int showOngoingJobsLessThan) {
        super();
        this.task = task;
        this.paramsUnbound = paramsUnbound;
        this.jobState = taskState;
        this.jobDescription = taskDescription;
        this.logger =
                new JobStartStopLogger(
                        "Job", loggerMonitor, monitor, false, showOngoingJobsLessThan);
    }

    @Override
    public Optional<JobExecutionException> call() {

        try {
            Task<T, S> taskDup = task.duplicateBean();

            jobState.markAsExecuting();

            logger.logStart(jobDescription);

            boolean success = taskDup.executeJob(paramsUnbound);

            jobState.markAsCompleted(success);
            logger.logEnd(jobDescription, jobState, success);
            return Optional.empty();

        } catch (Exception e) {
            // If executeTask is called with supressException==TRUE then Exceptions shouldn't occur
            // here as a rule from specific-tasks,
            //   as they should be logged internally to task-log. So if any error is actually thrown
            // here, let's consider it suspciously
            //
            // If executeTask is called with supressException==FALSE then we arrive here fairly,
            // easily, and record the error in the experiment-log just
            //  in case, even though it's probably already in the task log.

            ErrorReporterIntoLog errorReporter =
                    new ErrorReporterIntoLog(
                            paramsUnbound.getParametersExperiment().getLoggerExperiment());
            errorReporter.recordError(CallableJob.class, e);

            jobState.markAsCompleted(false);
            logger.logEnd(jobDescription, jobState, false);

            return Optional.of(new JobExecutionException(e));
        }
    }
}
