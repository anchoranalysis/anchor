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
package org.anchoranalysis.experiment.time;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Divider;
import org.anchoranalysis.core.time.RecordedExecutionTimes;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Generates report of the execution-times of different operations.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DescribeExecutionTimeStatistics {

    private static final Divider DIVIDER = new Divider();

    private static final String AVERAGE_TOTAL_LINE =
            "\t\t\t\t\t\t\t\t\t\taverage       total      (ignoring any parallelism)";

    /**
     * Describes how long operations took to complete en aggregate with related-statistics.
     *
     * @param taskTotal how long the task in total took.
     * @param operations how long particular operations took to execute.
     * @param executionTimeTotal the total execution time.
     * @return a multi-line string which describes the operations in a human-readable way.
     */
    public static String describeExecutionTimes(
            RunningSum taskTotal, RecordedExecutionTimes operations, long executionTimeTotal) {

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
                DescribeOperations.individual(
                        RecordedOperationHelper.create("* Entire Job *", taskTotal)));
        builder.append(System.lineSeparator());
        builder.append(DescribeOperations.allOperations(operations));
        builder.append(DIVIDER.withoutLabel());
        return builder.toString();
    }
}
