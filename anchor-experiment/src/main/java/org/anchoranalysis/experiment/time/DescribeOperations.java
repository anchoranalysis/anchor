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
import org.anchoranalysis.core.time.RecordedExecutionTimes;
import org.anchoranalysis.core.value.LanguageUtilities;
import org.anchoranalysis.core.value.StringUtilities;
import org.apache.commons.lang3.StringUtils;

/**
 * Describe the execution times in nicely formatted user strings.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DescribeOperations {

    /** The number of characters the identifier should be padded to. */
    private static final int WIDTH_IDENTIFIER = 51;

    /** The number of characters the execution time should be padded to. */
    private static final int WIDTH_EXECUTION_TIME = 13;

    /**
     * Describes all operations whose execution-times were recorded.
     *
     * @param operationStatistics the operations and associated execution-times.
     * @return a String describing the operations.
     */
    public static String allOperations(RecordedExecutionTimes operationStatistics) {
        if (!operationStatistics.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            operationStatistics.forEach(recorded -> builder.append(individual(recorded)));
            return builder.toString();
        } else {
            return "";
        }
    }

    /**
     * Describes an individual execution time.
     *
     * @param operation the operation.
     * @return a String describing the individual execution time, with a newline at the end.
     */
    public static String individual(RecordedExecutionTimes.RecordedOperation operation) {

        String prefix = StringUtils.repeat("..", operation.getNumberParentOperations());

        String paddedIdentifier =
                StringUtilities.rightPad(
                        prefix + operation.getOperationIdentifier(), WIDTH_IDENTIFIER);
        String suffix =
                String.format(
                        "across %d %s",
                        operation.getCount(),
                        LanguageUtilities.pluralizeMaybe(operation.getCount(), "instance"));
        return String.format(
                "%s\t = %s%s\t%s%n",
                paddedIdentifier,
                describeTime(operation.meanExecutionTimeSeconds()),
                describeTime(operation.sumExecutionTimeSeconds()),
                suffix);
    }

    private static String describeTime(double executionTime) {
        return StringUtilities.leftPad(
                String.format("%.3f s", executionTime), WIDTH_EXECUTION_TIME);
    }
}
