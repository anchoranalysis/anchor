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

import java.util.function.DoubleSupplier;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.text.LanguageUtilities;
import org.anchoranalysis.experiment.task.TaskStatistics;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class StatisticsLogger {

    private static final String JOB_WORD = "job";

    private MessageLogger logger;
    
    /**
     * Logs a textual message describing the statistics
     *
     * @param stats the statistics
     */
    public void logTextualMessage(TaskStatistics stats) {

        if (stats.allSuccessful()) {
            logMessageAboutTasks(
                    "All ",
                    stats.numberCompletedSuccess(),
                    " completed successfully.",
                    stats::meanExecutionTimeSuccess);
        } else {

            logMessageAboutTasks(
                    "",
                    stats.numberCompletedSuccess(),
                    String.format(
                            " out of %d completed successfully.", stats.numberTotalScheduledJobs()),
                    stats::meanExecutionTimeSuccess);

            logMessageAboutTasks(
                    "", stats.numberCompletedFailed(), " failed.", stats::meanExecutionTimeFailed);

            long numNotCompleted = stats.numberNotCompleted();
            if (numNotCompleted > 0) {
                logger.logFormatted(
                        "%s were never submitted.", maybePluralizeJobs(numNotCompleted));
            }
        }
    }

    /**
     * Logs a message in the form (treating the + as concatenation, and the %d as a placeholder for
     * number
     *
     * <p>prefix + %d tasks + suffix + messageAboutAverageExecutionTime
     *
     * <p>The word 'tasks' is smart about pluralization.
     *
     * @param prefix prefix
     * @param numTasks number of tasks
     * @param suffix suffix
     * @param executionTimeMs a function for calculating the mean-execution time
     */
    private void logMessageAboutTasks(
            String prefix, long numTasks, String suffix, DoubleSupplier executionTimeMs) {

        logMessageWithExecutionTime(
                prefix + maybePluralizeJobs(numTasks) + suffix,
                numTasks > 0,
                numTasks > 1,
                executionTimeMs);
    }

    /**
     * Logs a message with optional additional message about average-execution time
     *
     * @param msg the message to always log
     * @param showExecutionTime indicates whether to include the optional additional message or not
     * @param moreThanOneJob indicates that there was more than one job i.e. num_jobs >=2
     * @param executionTimeMs function for calculating the average-execution time in millisecond
     */
    private void logMessageWithExecutionTime(
            String msg,
            boolean showExecutionTime,
            boolean moreThanOneJob,
            DoubleSupplier executionTimeMs) {
        if (showExecutionTime) {
            double execTimeSeconds = executionTimeMs.getAsDouble() / 1000;
            msg =
                    msg
                            + String.format(
                                    " The %sexecution time was %.3f ms.",
                                    maybeIncludeAverage(moreThanOneJob), execTimeSeconds);
        }
        logger.log(msg);
    }

    private String maybeIncludeAverage(boolean moreThanOneJob) {
        return moreThanOneJob ? "average " : "";
    }

    /**
     * Returns 1 task or n tasks as is appropriate
     *
     * @param number the number n
     * @return the string as above
     */
    private static String maybePluralizeJobs(long number) {
        return LanguageUtilities.prefixPluralizeMaybe(number, JOB_WORD);
    }
}
