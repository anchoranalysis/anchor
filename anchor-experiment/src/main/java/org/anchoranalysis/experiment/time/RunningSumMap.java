/*-
 * #%L
 * anchor-math
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * A map of unique identifiers to {@link RunningSum} instances.
 *
 * <p>The map is initially empty, and when an unrecognized identifier is passed to the {@link #get}
 * function, then it is added to the map.
 *
 * <p>It is sorted according to the natural order of {@code T}.
 *
 * <p>It is not thread-safe.
 *
 * @param <T> unique identifier.
 * @author Owen Feehan
 */
class RunningSumMap<T> {

    /** The underlying map, that preserves insertion order. */
    private Map<T, RunningSumParented> map = new LinkedHashMap<>();

    /**
     * Whether the map contains the key {@code key}?
     *
     * @param key the key.
     * @return true iff the map contains {@code key}.
     */
    public boolean containsKey(T key) {
        return map.containsKey(key);
    }

    /**
     * The {@link RunningSum} corresponding to a particular key.
     *
     * <p>A new {@link RunningSum} is created if it does not already exist.
     *
     * @param key index of the item.
     * @param numberParentOperations the number of parent operations, to be used when creating a new
     *     {@link RunningSum}. Irrelevant if the operation already exists.
     * @return the associated (either already existing or newly created) {@link RunningSum}.
     */
    public RunningSum get(T key, int numberParentOperations) {
        return map.computeIfAbsent(key, value -> new RunningSumParented(numberParentOperations))
                .getRunningSum();
    }

    /** Resets all items to zero. */
    public void reset() {
        map.forEach((key, value) -> value.getRunningSum().reset());
    }

    /**
     * Calculate the mean of each item and reset to zero.
     *
     * @return an array with a mean corresponding to each item in the collection.
     */
    public Map<T, Double> meanAndReset() {
        return map.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Entry::getKey,
                                entry -> entry.getValue().getRunningSum().meanAndReset()));
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
    public Set<Entry<T, RunningSumParented>> entrySet() {
        return map.entrySet();
    }
}
