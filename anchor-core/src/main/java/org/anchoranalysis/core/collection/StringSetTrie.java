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
