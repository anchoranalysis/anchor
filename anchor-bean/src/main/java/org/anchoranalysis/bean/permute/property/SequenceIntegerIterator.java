/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a sequence of integers to satisfy {@code start <= i <= end} (in certain increments)
 *
 * @author Owen Feehan
 */
class SequenceIntegerIterator implements Iterator<Integer> {

    private final int end;
    private final int increment;

    private int current;

    public SequenceIntegerIterator(int start, int end, int increment) {
        super();
        this.current = start;
        this.end = end;
        this.increment = increment;
    }

    @Override
    public boolean hasNext() {
        return current <= end;
    }

    @Override
    public Integer next() {

        int out = current;

        if (current > end) {
            throw new NoSuchElementException();
        }

        current += increment;

        return out;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
