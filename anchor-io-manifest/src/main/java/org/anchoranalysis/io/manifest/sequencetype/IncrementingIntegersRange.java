package org.anchoranalysis.io.manifest.sequencetype;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor class IncrementingIntegersRange implements IncompleteElementRange, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // START REQUIRED ARGUMENTS
    @Getter private final int start;

    @Getter private final int incrementSize;
    // END REQUIRED ARGUMENTS
    
    @Getter
    private int end = -1;
    
    public void increment() {

        // If end has been set, we always increment
        if (end == -1) {
            end = start;
        } else {
            end += incrementSize;
        }
    }
    
    public void setEnd(int end) {

        if (start == -1) {
            throw new IllegalArgumentException("Please call setStart before setEnd");
        }

        if (((end - start) % incrementSize) != 0) {
            throw new IllegalArgumentException("End is not incrementally reachable from start");
        }
        this.end = end;
    }
    
    @Override
    public int nextIndex(int indexIn) {
        double numPeriods = indexToNumberPeriods(indexIn);
        int indexOut = numberPeriodsToIndex(Math.floor(numPeriods + 1));
        if (indexOut <= getMaximumIndex()) {
            return indexOut;
        } else {
            return -1;
        }
    }

    @Override
    public int previousIndex(int indexIn) {
        double numPeriods = indexToNumberPeriods(indexIn);
        int indexOut = numberPeriodsToIndex(Math.ceil(numPeriods - 1));
        if (indexOut >= getMinimumIndex()) {
            return indexOut;
        } else {
            return -1;
        }
    }

    @Override
    public int previousEqualIndex(int index) {

        double numPeriods = indexToNumberPeriods(index);
        if (Math.floor(numPeriods) == numPeriods) {
            return index;
        } else {
            return previousIndex(index);
        }
    }

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return end;
    }
    
    @Override
    public String stringRepresentationForElement(int elementIndex) {
        return String.valueOf(elementIndex);
    }
    
    public int getNumberElements() {
        return (getRangeIndex() / incrementSize) + 1;
    }
    
    public int indexToNumberPeriodsInt(int index) {
        int norm = (index - start);
        return norm / incrementSize;
    }
    
    private int getRangeIndex() {
        return getMaximumIndex() - getMinimumIndex() + 1;
    }

    private double indexToNumberPeriods(int index) {
        int norm = (index - start);
        return ((double) norm) / incrementSize;
    }

    private int numberPeriodsToIndex(double numPeriods) {
        return (int) (numPeriods * incrementSize) + start;
    }
}