/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.bundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Bundle<T extends Serializable> implements Serializable, Iterable<BundleItem<T>> {

    /** */
    private static final long serialVersionUID = 508875270985307740L;

    private List<BundleItem<T>> list;

    private int capacity;

    public Bundle() {
        super();
        list = new LinkedList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void add(String index, T object) {
        this.list.add(new BundleItem<>(index, object));
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public Iterator<BundleItem<T>> iterator() {
        return list.iterator();
    }

    public Map<Integer, T> createHashMap() {
        HashMap<Integer, T> hashMap = new HashMap<>();
        for (BundleItem<T> item : list) {
            hashMap.put(Integer.parseInt(item.getIndex()), item.getObject());
        }
        return hashMap;
    }
}
