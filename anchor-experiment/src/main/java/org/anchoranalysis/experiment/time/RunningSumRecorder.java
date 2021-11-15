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
class RunningSumRecorder implements ExecutionTimeRecorder {

    /** Running sums are maintained for operations, identified uniquely by strings. */
    private RunningSumMap<String> map = new RunningSumMap<>();

    @Override
    public void recordExecutionTime(String operationIdentifier, long millis) {
        map.get(operationIdentifier).increment(millis);
    }

    @Override
    public void recordExecutionTime(
            String operationIdentifierFirst, String operationIdentiferSubsequent, long millis) {
        String identifier =
                map.containsKey(operationIdentifierFirst)
                        ? operationIdentiferSubsequent
                        : operationIdentifierFirst;
        map.get(identifier).increment(millis);
    }

    /**
     * Calculate the mean of each item and reset to zero.
     *
     * @return an array with a mean corresponding to each item in the collection.
     */
    public Map<String, Double> meanAndReset() {
        return map.meanAndReset();
    }

    /**
     * Are there no entries in the map?
     *
     * @return true if the map has no entries, false otherwise.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public RecordedExecutionTimes recordedTimes() {
        return new RecordedExecutionTimes(
                map.entrySet().stream()
                        .map(
                                entry ->
                                        RecordedOperationHelper.create(
                                                entry.getKey(), entry.getValue())));
    }
}
