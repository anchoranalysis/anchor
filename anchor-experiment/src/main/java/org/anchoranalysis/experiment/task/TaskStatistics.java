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
