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
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.core.time.RecordedExecutionTimes;
import org.anchoranalysis.math.arithmetic.RunningSumMap;

/**
 * Profiles how long the execution-time of particular operations takes.
 *
 * @author Owen Feehan
 */
class RunningSumRecorder extends ExecutionTimeRecorder {

    /** Running sums are maintained for operations, identified uniquely by strings. */
    private RunningSumMap<String> map = new RunningSumMap<>();

    @Override
    public void recordExecutionTime(String operationIdentifier, long millis) {
        synchronized (map) {
            map.get(operationIdentifier).increment(millis);
        }
    }

    @Override
    public void recordExecutionTime(
            String operationIdentifierFirst, String operationIdentiferSubsequent, long millis) {
        synchronized (map) {
            String identifier =
                    map.containsKey(operationIdentifierFirst)
                            ? operationIdentiferSubsequent
                            : operationIdentifierFirst;
            map.get(identifier).increment(millis);
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
                    map.entrySet().stream()
                            .map(
                                    entry ->
                                            RecordedOperationHelper.create(
                                                    entry.getKey(), entry.getValue())));
        }
    }
}
