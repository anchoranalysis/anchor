/*-
 * #%L
 * anchor-io-manifest
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.manifest.sequencetype;

import java.io.Serializable;
import java.util.TreeSet;
import lombok.Getter;

class RangeFromIndexSet implements IncompleteElementRange, Serializable {

    /** */
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
