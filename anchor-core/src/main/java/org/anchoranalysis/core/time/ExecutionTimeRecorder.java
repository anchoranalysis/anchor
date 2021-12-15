/*-
 * #%L
 * anchor-core
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

import org.anchoranalysis.core.functional.checked.CheckedRunnable;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;

/**
 * Records the execution-time of particular operations.
 *
 * @author Owen Feehan
 */
public abstract class ExecutionTimeRecorder {

    /**
     * The execution-times that have been recorded.
     *
     * @return newly created {@link RecordedExecutionTimes} that describes the execution-times.
     */
    public abstract RecordedExecutionTimes recordedTimes();

    /**
     * Records the execution-time of a particular operation.
     *
     * @param operationIdentifier a string uniquely identifying the operation.
     * @param millis how long the operation took in milliseconds.
     */
    public abstract void recordExecutionTime(String operationIdentifier, long millis);

    /**
     * Has a particular operation already been recorded?
     *
     * @param operationIdentifier a string uniquely identifying the operation.
     * @return true if the operation has already been recorded at least once, false otherwise.
     */
    public abstract boolean isOperationAlreadyRecorded(String operationIdentifier);

    /**
     * Executes an {@code operation} while recording the execution-time - <b>without a return type,
     * and without throwing an exception.</b>.
     *
     * <p>Operation times are always executed, even they throw an Exception or otherwise end in
     * failure.
     *
     * @param operationIdentifier an identifier for this type of write-operation, under which
     *     execution-times are aggregated.
     * @param operation the operation to execute
     */
    public void recordExecutionTimeUnchecked(String operationIdentifier, Runnable operation) {
        long startTimestamp = measureTime(true, operationIdentifier);
        try {
            operation.run();
        } finally {
            recordTimeDifferenceFrom(operationIdentifier, startTimestamp);
        }
    }

    /**
     * Executes an {@code operation} while recording the execution-time - <b>without a return
     * type</b>.
     *
     * <p>Operation times are always executed, even they throw an Exception or otherwise end in
     * failure.
     *
     * @param operationIdentifier an identifier for this type of write-operation, under which
     *     execution-times are aggregated.
     * @param operation the operation to execute
     * @param <E> type of an exception that {@code operation} may throw.
     * @throws E if {@code operation} throws this exception;
     */
    public <E extends Exception> void recordExecutionTime(
            String operationIdentifier, CheckedRunnable<E> operation) throws E {
        long startTimestamp = measureTime(true, operationIdentifier);
        try {
            operation.run();
        } finally {
            recordTimeDifferenceFrom(operationIdentifier, startTimestamp);
        }
    }

    /**
     * Executes an {@code operation} while recording the execution-time - <b>with a return type</b>.
     *
     * <p>Operation times are always executed, even they throw an Exception or otherwise end in
     * failure.
     *
     * @param operationIdentifier an identifier for this type of write-operation, under which
     *     execution-times are aggregated.
     * @param operation the operation to execute
     * @param <T> return-type of {@code operation}.
     * @param <E> type of an exception that {@code operation} may throw.
     * @return the value returned by {@code operation}.
     * @throws E if {@code operation} throws this exception,
     */
    public <T, E extends Exception> T recordExecutionTime(
            String operationIdentifier, CheckedSupplier<T, E> operation) throws E {
        long startTimestamp = measureTime(true, operationIdentifier);
        try {
            return operation.get();
        } finally {
            recordTimeDifferenceFrom(operationIdentifier, startTimestamp);
        }
    }

    /**
     * Records the execution-time of a particular operation by subtracting the start-time from the
     * end time.
     *
     * @param operationIdentifier a string uniquely identifying the operation.
     * @param startTimestamp a system clock timestamp (in milliseconds since the epoch) for when the
     *     operation <b>began</b>.
     * @param endTimestamp a system clock timestamp (in milliseconds since the epoch) for when the
     *     operation <b>ended</b>.
     */
    public void recordTimeDifferenceBetween(
            String operationIdentifier, long startTimestamp, long endTimestamp) {
        long executionTime = endTimestamp - startTimestamp;
        recordExecutionTime(operationIdentifier, executionTime);
    }

    /**
     * Indicates {@code operationIdentifier} is being recorded, and returns the current clock
     * timestamp.
     *
     * @param start true, if this time indicates the start of an operation. false, if it only
     *     describes the end.
     * @param operationIdentifiers all possible identifiers that may subsequently be used to record
     *     an end-time for this operation.
     * @return the a system clock timestamp (in milliseconds since the epoch).
     */
    public abstract long measureTime(boolean start, String... operationIdentifiers);

    /**
     * Records the execution-time of a particular operation by subtracting the start-time from the
     * current clock.
     *
     * @param operationIdentifier a string uniquely identifying the operation, and which must have
     *     been previously used in a call to {@link #measureTime} with {@code start==true}.
     * @param startTimestamp a system clock timestamp (in milliseconds since the epoch) for when the
     *     operation began.
     */
    private void recordTimeDifferenceFrom(String operationIdentifier, long startTimestamp) {
        long currentTimestamp = measureTime(false, operationIdentifier);
        long executionTime = currentTimestamp - startTimestamp;
        recordExecutionTime(operationIdentifier, executionTime);
    }
}
