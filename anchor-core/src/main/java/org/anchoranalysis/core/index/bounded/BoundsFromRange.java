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

package org.anchoranalysis.core.index.bounded;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.GetterFromIndex;

@AllArgsConstructor
public class BoundsFromRange<T> implements GetterFromIndex<T>, BoundedIndexContainer<T> {

    private GetterFromIndex<T> typedIndexGetter;
    private BoundedRangeIncomplete sequenceType;

    @Override
    public T get(int index) throws GetOperationFailedException {
        return typedIndexGetter.get(index);
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        // ASSUME STATIC
    }

    @Override
    public int nextIndex(int index) {
        return sequenceType.nextIndex(index);
    }

    @Override
    public int previousIndex(int index) {
        return sequenceType.previousIndex(index);
    }

    @Override
    public int previousEqualIndex(int index) {
        return sequenceType.previousEqualIndex(index);
    }

    @Override
    public int getMinimumIndex() {
        return sequenceType.getMinimumIndex();
    }

    @Override
    public int getMaximumIndex() {
        return sequenceType.getMaximumIndex();
    }
}
