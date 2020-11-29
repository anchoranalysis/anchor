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

package org.anchoranalysis.experiment.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Maintains statistics on the execution of a task (considering jobs in aggregate)
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class TaskStatistics {

    // START REQUIRED ARGUMENTS
    /** Total number of scheduled jobs. */
    @Getter private final long numberTotalScheduledJobs;

    /** Execution time (in milliseconds) for of tasks that successfully completed. */
    private final RunningSum success;

    /** Execution time (in milliseconds) for of tasks that failed. */
    private final RunningSum failed;
    // END REQUIRED ARGUMENTS

    /**
     * Number of jobs that have not been completed.
     *
     * @return
     */
    public long numberNotCompleted() {
        long numberNotCompleted =
                numberTotalScheduledJobs - numberCompletedSuccess() - numberCompletedFailed();
        assert numberNotCompleted >= 0;
        return numberNotCompleted;
    }

    /**
     * Mean execution-time of successfully-completed jobs in milliseconds.
     *
     * @return the mean-time
     */
    public double meanExecutionTimeSuccess() {
        return success.mean();
    }

    /**
     * Mean execution-time of failed jobs in milliseconds.
     *
     * @return the mean-time
     */
    public double meanExecutionTimeFailed() {
        return failed.mean();
    }

    /**
     * Did all jobs execute and completely successfully?
     *
     * @return true if yes, false if no
     */
    public boolean allSuccessful() {
        return numberCompletedSuccess() == numberTotalScheduledJobs;
    }

    public long numberCompletedSuccess() {
        return success.getCount();
    }

    public long numberCompletedFailed() {
        return failed.getCount();
    }
}
