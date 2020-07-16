/* (C)2020 */
package org.anchoranalysis.io.manifest.sequencetype;

import java.util.TreeSet;
import org.anchoranalysis.core.index.container.OrderProvider;

public class ChangeSequenceType extends SequenceType {

    /** */
    private static final long serialVersionUID = -4134961949858208220L;

    private TreeSet<Integer> indexSet = new TreeSet<>();

    private int minimumIndex = -1;

    private int maximumIndex = -1;

    @Override
    public String getName() {
        return "change";
    }

    // Assumes updates are sequential
    @Override
    public void update(String indexStr) throws SequenceTypeException {

        int index = Integer.parseInt(indexStr);

        if (maximumIndex != -1 && index <= maximumIndex) {
            throw new SequenceTypeException("indexes are not being addded sequentially");
        }

        maximumIndex = index;
        indexSet.add(index);

        if (minimumIndex == -1) {
            minimumIndex = index;
        }
    }

    @Override
    public int nextIndex(int index) {

        Integer next = indexSet.higher(index);
        if (next != null) {
            return next;
        } else {
            return -1;
        }
    }

    @Override
    public int previousIndex(int index) {

        Integer prev = indexSet.lower(index);
        if (prev != null) {
            return prev;
        } else {
            return -1;
        }
    }

    @Override
    public int previousEqualIndex(int index) {

        if (indexSet.contains(index)) {
            return index;
        } else {
            return previousIndex(index);
        }
    }

    @Override
    public int getMinimumIndex() {
        return minimumIndex;
    }

    @Override
    public int getMaximumIndex() {
        return maximumIndex;
    }

    public void setMaximumIndex(int maximumIndex) {
        if (maximumIndex > this.maximumIndex) {
            this.maximumIndex = maximumIndex;
        }
    }

    @Override
    public OrderProvider createOrderProvider() {
        OrderProviderHashMap map = new OrderProviderHashMap();
        map.addIntegerSet(indexSet);
        return map;
    }

    @Override
    public int getNumElements() {
        return indexSet.size();
    }

    @Override
    public String indexStr(int index) {
        return String.valueOf(index);
    }
}
