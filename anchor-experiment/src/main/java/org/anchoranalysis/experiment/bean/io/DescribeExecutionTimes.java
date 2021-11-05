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

import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.value.LanguageUtilities;
import org.anchoranalysis.experiment.task.ExecutionTimeStatistics;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Describe the execution times in nicely formatted user strings.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DescribeExecutionTimes {

    /** The number of characters the identifier should be padded to. */
    private static final int WIDTH_IDENTIFIER = 35;

    /** The number of characters the execution time should be padded to. */
    private static final int WIDTH_EXECUTION_TIME = 13;

    /**
     * Describes all operations recorded in a {@link ExecutionTimeStatistics}.
     *
     * @param operationStatistics the operations.
     * @return a String describing the operations.
     */
    public static String allOperations(ExecutionTimeStatistics operationStatistics) {
        if (!operationStatistics.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Entry<String, RunningSum> entry : operationStatistics.entrySet()) {
                RunningSum runningSum = entry.getValue();
                builder.append(
                        individual(
                                entry.getKey(),
                                runningSum.mean() / 1000,
                                runningSum.getSum() / 1000,
                                (int) runningSum.getCount()));
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    /**
     * Describes an individual execution time.
     *
     * @param identifier the identifier describing the execution time.
     * @param averageExecutionTime the <b>average execution time</b> in seconds.
     * @param totalExecutionTime the <b>total execution time</b> in seconds.
     * @param count the number of entities the executionTime refers to.
     * @return a String describing the individual execution time, with a newline at the end.
     */
    public static String individual(
            String identifier, double averageExecutionTime, double totalExecutionTime, int count) {
        String paddedIdentifier = rightPadding(identifier, WIDTH_IDENTIFIER);
        String suffix =
                String.format(
                        "across %d %s", count, LanguageUtilities.pluralizeMaybe(count, "instance"));
        return String.format(
                "%s\t = %s%s\t%s%n",
                paddedIdentifier,
                describeTime(averageExecutionTime),
                describeTime(totalExecutionTime),
                suffix);
    }

    private static String describeTime(double executionTime) {
        return leftPadding(String.format("%.3f s", executionTime), WIDTH_EXECUTION_TIME);
    }

    /** Left-pad a string with spaces, so it reaches a fixed length. */
    private static String leftPadding(String string, int number) {
        return String.format("%1$" + number + "s", string); // NOSONAR
    }

    /** Right-pad a string with spaces, so it reaches a fixed length. */
    private static String rightPadding(String string, int number) {
        return String.format("%1$-" + number + "s", string); // NOSONAR
    }
}
