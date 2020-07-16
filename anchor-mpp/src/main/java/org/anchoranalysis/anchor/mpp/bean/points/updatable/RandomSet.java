/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.points.updatable;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;

// http://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
@EqualsAndHashCode(callSuper = false)
class RandomSet<E> extends AbstractSet<E> {

    private List<E> dta = new ArrayList<>();
    private Map<E, Integer> idx = new HashMap<>();

    @Override
    public boolean add(E item) {
        if (idx.containsKey(item)) {
            return false;
        }
        idx.put(item, dta.size());
        dta.add(item);
        return true;
    }

    /**
     * Override element at position <code>id</code> with last element.
     *
     * @param id
     */
    public void removeAt(int id) {
        if (id >= dta.size()) {
            return;
        }
        E res = dta.get(id);
        idx.remove(res);
        E last = dta.remove(dta.size() - 1);
        // skip filling the hole if last is removed
        if (id < dta.size()) {
            idx.put(last, id);
            dta.set(id, last);
        }
    }

    @Override
    public boolean remove(Object item) {
        Integer id = idx.get(item);
        if (id == null) {
            return false;
        }
        removeAt(id);
        return true;
    }

    public E get(int i) {
        return dta.get(i);
    }

    @Override
    public int size() {
        return dta.size();
    }

    @Override
    public Iterator<E> iterator() {
        return dta.iterator();
    }
}
