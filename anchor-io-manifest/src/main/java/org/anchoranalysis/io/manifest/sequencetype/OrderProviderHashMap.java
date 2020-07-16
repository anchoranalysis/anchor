/* (C)2020 */
package org.anchoranalysis.io.manifest.sequencetype;

import java.util.HashMap;
import java.util.Set;
import org.anchoranalysis.core.index.container.OrderProvider;

public class OrderProviderHashMap implements OrderProvider {

    // Maps each index to its bundle index
    private HashMap<String, Integer> indexHash;

    public OrderProviderHashMap() {
        indexHash = new HashMap<>();
    }

    public void addIntegerSet(Set<Integer> set) {

        // We iterate over every element assigning them
        // the correct bundle index
        int counter = 0;
        for (Integer index : set) {
            indexHash.put(String.valueOf(index), counter++);
        }
    }

    public void addStringSet(Set<String> set) {

        // We iterate over every element assigning them
        // the correct bundle index
        int counter = 0;
        for (String index : set) {
            indexHash.put(index, counter++);
        }
    }

    @Override
    public int order(String index) {
        Integer o = indexHash.get(index);
        if (o == null) {
            throw new IndexOutOfBoundsException();
        }
        return o;
    }
}
