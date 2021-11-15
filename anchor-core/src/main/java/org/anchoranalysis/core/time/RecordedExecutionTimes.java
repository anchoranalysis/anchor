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
package org.anchoranalysis.core.time;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Recorded execution-times for operations.
 *
 * @author Owen Feehan
 */
public class RecordedExecutionTimes {

    /** A particular type of operation that has been recorded (en aggregate). */
    @AllArgsConstructor
    public static class RecordedOperation {

        /** An (unique) identifier for the operation. */
        @Getter private final String operationIdentifier;

        /** The mean execution time in <i>milliseconds</i> for the operation. */
        private final double meanExecutionTime;

        /** The total summed execution time in <i>milliseconds</i> for the operation. */
        private final double sumExecutionTime;

        /** How many times the operation occurred. */
        @Getter private final int count;

        /**
         * The mean execution time in <i>seconds</i> for the operation.
         *
         * @return the mean execution time.
         */
        public double meanExecutionTimeSeconds() {
            return meanExecutionTime / 1000;
        }

        /**
         * The total summed execution time in <i>seconds</i> for the operation.
         *
         * @return the mean execution time.
         */
        public double sumExecutionTimeSeconds() {
            return sumExecutionTime / 1000;
        }
    }

    /**
     * A set of each operation-identifiers and corresponding execution-times.
     *
     * <p>This set is considered non-mutable within this class, so its state should never changed by
     * this class.
     */
    private final Set<RecordedOperation> runningTimes;

    /**
     * Create with the running-times.
     *
     * @param runningTimes a set of each operation-identifiers and corresponding execution-times.
     */
    public RecordedExecutionTimes(Stream<RecordedOperation> runningTimes) {
        this.runningTimes = runningTimes.collect(Collectors.toSet());
    }

    /**
     * For each unique operation with a recorded-time, call {@code consumer}.
     *
     * @param consumer called for each unique operation that has been recorded.
     */
    public void forEach(Consumer<RecordedOperation> consumer) {
        runningTimes.stream().forEach(consumer::accept);
    }

    /**
     * Are there no recorded operations?
     *
     * @return yes, iff no recorded operations exist.
     */
    public boolean isEmpty() {
        return runningTimes.isEmpty();
    }
}
