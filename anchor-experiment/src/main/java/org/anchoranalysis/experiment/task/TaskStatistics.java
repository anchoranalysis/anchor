/* (C)2020 */
package org.anchoranalysis.experiment.task;

/**
 * Maintains statistics on the execution of a task (considering jobs in aggregate)
 *
 * @author Owen Feehan
 */
public class TaskStatistics {

    /** Total number of scheduled jobs */
    private long numTotalScheduledJobs;

    /** Number of tasks that successfully completed */
    private long numCompletedSuccess = 0;

    /** Number of tasks that failed */
    private long numCompletedFailed = 0;

    /** Total execution time in milliseconds on successful tasks */
    private long sumExecutionTimeSuccess;

    /** Total execution time in milliseconds on failed tasks */
    private long sumExecutionTimeFailed;

    public TaskStatistics(int numTotalScheduledJobs) {
        super();
        this.numTotalScheduledJobs = numTotalScheduledJobs;
    }

    /**
     * Increases the number of successful jobs by one
     *
     * @param executionTimeMs the execution-time in milliseconds of the successful job
     */
    public void incrSuccess(long executionTimeMs) {
        numCompletedSuccess++;
        sumExecutionTimeSuccess += executionTimeMs;
    }

    /**
     * Increases the number of failed jobs by one
     *
     * @param executionTimeMs the execution-time in milliseconds of the failed job
     */
    public void incrFailed(long executionTimeMs) {
        numCompletedFailed++;
        sumExecutionTimeFailed += executionTimeMs;
    }

    public void setNumCompletedSuccess(long numSuccess, long sumExecutionTimeSuccess) {
        this.numCompletedSuccess = numSuccess;
        this.sumExecutionTimeSuccess = sumExecutionTimeSuccess;
    }

    public void setNumCompletedFailed(long numFailed, long sumExecutionTimeFailed) {
        this.numCompletedFailed = numFailed;
        this.sumExecutionTimeFailed = sumExecutionTimeFailed;
    }

    public long numTotalScheduledJobs() {
        return numTotalScheduledJobs;
    }

    public long numCompletedSuccess() {
        return numCompletedSuccess;
    }

    public long numCompletedFailed() {
        return numCompletedFailed;
    }

    public long numNotCompleted() {
        long numNotCompleted = numTotalScheduledJobs - numCompletedSuccess - numCompletedFailed;
        assert numNotCompleted >= 0;
        return numNotCompleted;
    }

    /** @return mean execution-time of successfully-completed jobs in milliseconds */
    public double meanExecutionTimeSuccess() {
        double sumExecTime = (double) sumExecutionTimeSuccess;
        return sumExecTime / numCompletedSuccess;
    }

    /** @return mean execution-time of failed jobs in milliseconds */
    public double meanExecutionTimeFailed() {
        double sumExecTime = (double) sumExecutionTimeFailed;
        return sumExecTime / numCompletedFailed;
    }

    /**
     * Did all jobs execute and completely successfully?
     *
     * @return true if yes, false if no
     */
    public boolean isAllSuccessful() {
        return numCompletedSuccess == numTotalScheduledJobs;
    }
}
