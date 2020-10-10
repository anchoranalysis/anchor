package org.anchoranalysis.io.manifest.sequencetype;

import java.io.Serializable;
import java.util.TreeSet;
import lombok.Getter;

class RangeFromIndexSet implements IncompleteElementRange, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Getter private TreeSet<Integer> set = new TreeSet<>();

    private int minimumIndex = -1;

    private int maximumIndex = -1;
    
    public void update(Integer element) throws SequenceTypeException {

        if (maximumIndex != -1 && element <= maximumIndex) {
            throw new SequenceTypeException("indexes are not being addded sequentially");
        }

        maximumIndex = element;
        set.add(element);

        if (minimumIndex == -1) {
            minimumIndex = element;
        }
    }
    
    public int getNumberElements() {
        return set.size();
    }
    
    public void assignMaximumIndex(int index) {
        if (index > this.maximumIndex) {
            this.maximumIndex = index;
        }
    }
    
    @Override
    public int previousIndex(int index) {

        Integer prev = set.lower(index);
        if (prev != null) {
            return prev;
        } else {
            return -1;
        }
    }

    @Override
    public int previousEqualIndex(int index) {

        if (set.contains(index)) {
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
    
    @Override
    public int nextIndex(int index) {

        Integer next = set.higher(index);
        if (next != null) {
            return next;
        } else {
            return -1;
        }
    }

    @Override
    public String stringRepresentationForElement(int elementIndex) {
        return String.valueOf(elementIndex);
    }
}