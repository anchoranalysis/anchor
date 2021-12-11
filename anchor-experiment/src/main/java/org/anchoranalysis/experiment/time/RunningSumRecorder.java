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

import java.util.Map;
import java.util.Map.Entry;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.core.time.RecordedExecutionTimes;
import org.anchoranalysis.core.time.RecordedExecutionTimes.RecordedOperation;

/**
 * Profiles how long the execution-time of particular operations takes.
 *
 * <p>A thread-local variable keeps track of how many open recording-operations exist, between the
 * call to {@link #measureTime} with {@code==true} and a subsequent call to {@link
 * #recordExecutionTime}, indicating the end of the operation.
 *
 * @author Owen Feehan
 */
class RunningSumRecorder extends ExecutionTimeRecorder {

    /** Running sums are maintained for operations, identified uniquely by strings. */
    private RunningSumMap<String> map = new RunningSumMap<>();

    /** For storing a counter on the number of ongoing operations. */
    private ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    @Override
    public void recordExecutionTime(String operationIdentifier, long millis) {
        synchronized (map) {
            int countOngoing = threadLocal.get();
            if (countOngoing <= 1) {
                threadLocal.remove();
            } else {
                threadLocal.set(countOngoing - 1);
            }
            map.get(operationIdentifier, countOngoing - 1).increment(millis);
        }
    }

    @Override
    public long measureTime(boolean start, String... operationIdentifiers) {
        if (start) {
            synchronized (map) {
                int countOngoing = threadLocal.get();
                // Even if multiple operation identifiers are passed, we only increment the counter
                // by 1
                //  assuming these are alternative identifiers
                try {
                    for (String identifier : operationIdentifiers) {
                        // Just by retrieving this RunningSum, it ensures it is added to the map at
                        // this
                        // this timepoint. This can be important to achieve the proper ordering of
                        // identifiers.
                        map.get(identifier, countOngoing);
                    }
                } finally {
                    threadLocal.set(countOngoing + 1);
                }
            }
        }
        return System.currentTimeMillis();
    }

    @Override
    public boolean isOperationAlreadyRecorded(String operationIdentifier) {
        synchronized (map) {
            return map.containsKey(operationIdentifier)
                    && map.get(operationIdentifier, 0).getCount() > 0;
        }
    }

    /**
     * Calculate the mean of each item and reset to zero.
     *
     * @return an array with a mean corresponding to each item in the collection.
     */
    public Map<String, Double> meanAndReset() {
        synchronized (map) {
            return map.meanAndReset();
        }
    }

    /**
     * Are there no entries in the map?
     *
     * @return true if the map has no entries, false otherwise.
     */
    public boolean isEmpty() {
        synchronized (map) {
            return map.isEmpty();
        }
    }

    @Override
    public RecordedExecutionTimes recordedTimes() {
        synchronized (map) {
            return new RecordedExecutionTimes(
                    map.entrySet().stream().map(RunningSumRecorder::recordedOperationFromEntry));
        }
    }

    /** Derives a {@link RecordedOperation} from a map entry. */
    private static RecordedOperation recordedOperationFromEntry(
            Entry<String, RunningSumParented> entry) {
        return RecordedOperationHelper.create(
                entry.getKey(),
                entry.getValue().getRunningSum(),
                entry.getValue().getNumberParentOperations());
    }
}
