/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.HashMap;

// Remembers the last number used in a sequence, associated with a particular string
public class SequenceMemory {

    private HashMap<String, Integer> map = new HashMap<>();

    public int lastIndex(String key) {
        return map.computeIfAbsent(key, k -> Integer.valueOf(0));
    }

    // Increments the index and returns the new incremented index
    public int indexIncr(String key) {
        int ind = lastIndex(key);
        ind++;
        updateIndex(key, ind);
        return ind;
    }

    public void updateIndex(String key, int indexNew) {

        map.remove(key);
        Integer index = Integer.valueOf(indexNew);
        map.put(key, index);
    }
}
