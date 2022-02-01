package org.anchoranalysis.core.collection;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;

/**
 * A tree map that creates a new item, if it doesn't already exist upon a <i>get</i> operation.
 *
 * <p>Internally it uses a {@link HashMap} for it's implementation, and the {@code K} and {@code V}
 * types must obey the rules for a {@link HashMap} (with valid equals, hashcode etc.)
 *
 * <p>This structure is not inherently thread-safe.
 *
 * @author Owen Feehan
 * @param <K> identifier (key)-type
 * @param <V> value-type
 */
public class MapCreate<K, V> {

    // We use a tree-map to retain a deterministic order in the keys, as outputting in alphabetic
    // order is nice
    private Map<K, V> map;

    /** How to create a new element, called when needed. */
    private Supplier<V> createNewElement;

    /**
     * Creates without a comparator, using a {@link HashMap} internally.
     *
     * @param createNewElement called as necessary to create a new element in the tree.
     */
    public MapCreate(Supplier<V> createNewElement) {
        this.map = new HashMap<>();
        this.createNewElement = createNewElement;
    }

    /**
     * Creates with an explicit comparator, and using a {@link TreeMap} internally.
     *
     * @param createNewElement called as necessary to create a new element in the tree.
     * @param comparator used to impose an ordering on elements.
     */
    public MapCreate(Supplier<V> createNewElement, Comparator<K> comparator) {
        this.map = new TreeMap<>(comparator);
        this.createNewElement = createNewElement;
    }

    /**
     * Gets an existing element from the map, returning null if it is absent.
     *
     * @param key the key for the map query.
     * @return an element, either retrieved from the map, or null, if none exists in the map.
     */
    public V get(K key) {
        return map.get(key);
    }

    /**
     * Gets an existing element from the map, newly creating and storing the element if it's absent.
     *
     * @param key the key for the map query.
     * @return an element, either retrieved from the map, or newly created.
     */
    public V computeIfAbsent(K key) {
        return map.computeIfAbsent(key, ignored -> createNewElement.get());
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to the specified
     * value.
     *
     * @param key the key to remove.
     * @param value the value to remove.
     */
    public void remove(K key, V value) {
        map.remove(key, value);
    }

    /**
     * The entries in the map.
     *
     * @return a set view of the entries contained in this map.
     */
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * The keys in the map.
     *
     * @return a set view of the keys contained in this map.
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Whether the map is empty or not.
     *
     * @return true iff the map is empty.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Iterate over each entry in the map, and apply an operation.
     *
     * @param <E> an exception that may be thrown by {code operation}.
     * @param operation the operation applied to each element in the map, passing the key and value
     *     as parameters.
     * @throws E if {@code operation} throws it.
     */
    public <E extends Exception> void iterateEntries(CheckedBiConsumer<K, V, E> operation)
            throws E {
        FunctionalIterate.iterateMap(map, operation);
    }
}
