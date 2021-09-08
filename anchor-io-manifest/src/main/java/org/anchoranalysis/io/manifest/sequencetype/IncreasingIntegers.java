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

/**
 * Indices must be integers that always increase as added.
 *
 * <p>A sorted-set records all such indices.
 *
 * @author Owen Feehan
 */
public class IncreasingIntegers extends SequenceType<Integer> {

    /** */
    private static final long serialVersionUID = -4134961949858208220L;

    private RangeFromIndexSet range = new RangeFromIndexSet();

    @Override
    public String getName() {
        return "change";
    }

    // Assumes updates are sequential
    @Override
    public void update(Integer element) throws SequenceTypeException {
        range.update(element);
    }

    @Override
    public OrderProvider createOrderProvider() {
        OrderProviderHashMap map = new OrderProviderHashMap();
        map.addIntegerSet(range.getSet());
        return map;
    }

    @Override
    public int getNumberElements() {
        return range.getNumberElements();
    }

    @Override
    public void assignMaximumIndex(int index) {
        range.assignMaximumIndex(index);
    }

    @Override
    public IncompleteElementRange elementRange() {
        return range;
    }
}
