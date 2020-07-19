package org.anchoranalysis.core.index.container;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.index.GetOperationFailedException;

/* Contains a sequence of integers from 0 until size-1 (inclusive) */
public class IntegerSequenceContaner implements BoundedIndexContainer<Integer> {

    private int size;

    public IntegerSequenceContaner(int size) {
        super();
        this.size = size;
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        // Never happens
    }

    @Override
    public int nextIndex(int index) {
        if (index < (size - 1)) {
            return index + 1;
        } else {
            return -1;
        }
    }

    @Override
    public int previousIndex(int index) {
        return index - 1;
    }

    @Override
    public int previousEqualIndex(int index) {
        return index;
    }

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return (size - 1);
    }

    @Override
    public Integer get(int index) throws GetOperationFailedException {

        if (index < 0 || index >= size) {
            throw new GetOperationFailedException(index,
                    String.format("Invalid index: %d", index));
        }

        return index;
    }
}
