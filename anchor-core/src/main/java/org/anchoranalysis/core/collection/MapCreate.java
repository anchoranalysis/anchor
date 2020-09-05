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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * A tree map that creates a new item, if it doesn't already exist, after a GET operation
 *
 * @author Owen Feehan
 * @param <K> key-type
 * @param <V> value-type
 */
public class MapCreate<K, V> {

    // We use a tree-map to retain a deterministic order in the keys, as outputting in alphabetic
    // order is nice
    private Map<K, V> map;
    private Supplier<V> createNewElement;

    public MapCreate(Supplier<V> createNewElement) {
        this.map = new TreeMap<>();
        this.createNewElement = createNewElement;
    }

    public MapCreate(Supplier<V> opCreateNew, Comparator<K> comparator) {
        super();
        this.map = new TreeMap<>(comparator);
        this.createNewElement = opCreateNew;
    }

    public synchronized V getOrCreateNew(K key) {
        return map.computeIfAbsent(key, k -> createNewElement.get());
    }

    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public Map<K, V> asMap() {
        return map;
    }
}
