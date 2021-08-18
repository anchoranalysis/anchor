package org.anchoranalysis.math.arithmetic;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * A map of unique identifiers to {@link RunningSum} instances.
 *
 * <p>The map is initially empty, and when an unrecognised identifier is passed to the {@link #get}
 * function, then it is added to the map.
 *
 * @param <T> unique identifier.
 * @author Owen Feehan
 */
public class RunningSumMap<T> {

    /**
     * The underlying map, that should be thread-safe, as different tasks can access in parallel.
     */
    private Map<T, RunningSum> map = new ConcurrentSkipListMap<>();

    /** Whether the map contains the key {@code key}. ? */
    public boolean containsKey(T key) {
        return map.containsKey(key);
    }

    /**
     * The {@link RunningSum} corresponding to a particular key.
     *
     * <p>A new {@link RunningSum} is created if it does not already exist.
     *
     * @param key index of the item
     * @return the individual item
     */
    public RunningSum get(T key) {
        return map.computeIfAbsent(key, value -> new RunningSum());
    }

    /** Resets all items to zero. */
    public void reset() {
        map.forEach((key, value) -> value.reset());
    }

    /**
     * Calculate the mean of each item and reset to zero.
     *
     * @return an array with a mean corresponding to each item in the collection.
     */
    public Map<T, Double> meanAndReset() {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().meanAndReset()));
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
    public Set<Entry<T, RunningSum>> entrySet() {
        return map.entrySet();
    }
}
