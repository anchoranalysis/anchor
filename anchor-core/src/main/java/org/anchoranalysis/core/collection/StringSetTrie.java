/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.core.collection;

import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 * A <a href="https://en.wikipedia.org/wiki/Trie">Trie / Prefix Tree</a> that stores {@link String}s
 * efficiently.
 *
 * @author Owen Feehan
 */
public class StringSetTrie {

    private PatriciaTrie<String> trie;

    /** Create with no elements. */
    public StringSetTrie() {
        trie = new PatriciaTrie<>();
    }

    /**
     * Create from existing collection.
     *
     * @param collection the collection to create from.
     */
    public StringSetTrie(Collection<String> collection) {
        this();
        for (String element : collection) {
            add(element);
        }
    }

    /**
     * Adds an string to the set.
     *
     * @param stringToAdd the string to add.
     */
    public void add(String stringToAdd) {
        trie.put(stringToAdd, null);
    }

    /**
     * Does the set contain a particular string?
     *
     * @param string the string to check.
     * @return true iff it is contained within the set.
     */
    public boolean contains(String string) {
        return trie.containsKey(string);
    }

    /**
     * Does the set contain no elements?
     *
     * @return if the set contains zero elements.
     */
    public boolean isEmpty() {
        return trie.size() == 0;
    }

    /**
     * A view over the values in the set.
     *
     * @return the values, avoiding creating a new object via caching.
     */
    public Set<String> values() {
        return trie.keySet();
    }
}
