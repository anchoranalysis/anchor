/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.experiment.bean.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.log.Divider;
import org.anchoranalysis.experiment.task.ExecutionTimeStatistics;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Generates report of the execution-times of different operations.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExecutionTimeStatisticsReport {

    private static final Divider DIVIDER = new Divider();

    private static final String AVERAGE_TOTAL_LINE =
            "\t\t\t\t\t\t\t\t\t\taverage       total      (ignoring any parallelism)";

    public static String report(
            TaskStatistics task, ExecutionTimeStatistics operation, long executionTimeTotal) {

        RunningSum taskTotal = task.executionTimeTotal();

        StringBuilder builder = new StringBuilder();
        builder.append(
                String.format(
                        "The total execution time was %d seconds, including any parallelism.",
                        executionTimeTotal));
        builder.append(System.lineSeparator());
        builder.append(DIVIDER.withLabel("Execution Time"));
        builder.append(System.lineSeparator());
        builder.append(AVERAGE_TOTAL_LINE);
        builder.append(System.lineSeparator());
        builder.append(
                DescribeExecutionTimes.individual(
                        "Entire Job",
                        taskTotal.mean() / 1000,
                        taskTotal.getSum() / 1000,
                        (int) task.numberCompletedTotal()));
        builder.append(DescribeExecutionTimes.allOperations(operation));
        builder.append(DIVIDER.withoutLabel());
        return builder.toString();
    }
}
