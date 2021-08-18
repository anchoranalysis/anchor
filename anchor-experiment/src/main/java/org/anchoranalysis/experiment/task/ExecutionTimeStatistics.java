package org.anchoranalysis.experiment.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.anchoranalysis.core.system.ExecutionTimeRecorder;
import org.anchoranalysis.math.arithmetic.RunningSum;
import org.anchoranalysis.math.arithmetic.RunningSumMap;

/**
 * Maintains statistics on how long particular operations take across jobs.
 *
 * @author Owen Feehan
 */
public class ExecutionTimeStatistics implements ExecutionTimeRecorder {

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
     * @return an array with a mean corresponding to each item in the collection
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

    /**
     * The entries in the underlying map.
     *
     * @return the entries in the map.
     */
    public Set<Entry<String, RunningSum>> entrySet() {
        return map.entrySet();
    }
}
