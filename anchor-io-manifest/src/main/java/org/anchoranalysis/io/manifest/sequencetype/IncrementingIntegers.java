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

import org.anchoranalysis.core.index.bounded.OrderProvider;

public class IncrementingIntegers extends SequenceType<Integer> {

    /** */
    private static final long serialVersionUID = -3896806957547954271L;

    private IncrementingIntegersRange range;

    public IncrementingIntegers() {
        this(0);
    }

    public IncrementingIntegers(int start) {
        this(start, 1);
    }

    public IncrementingIntegers(int start, int incrementSize) {
        this.range = new IncrementingIntegersRange(start, incrementSize);
    }

    @Override
    public String getName() {
        return "incremental";
    }

    @Override
    public void update(Integer element) throws SequenceTypeException {

        // if the element exists at the end of the current range, we assume its a repeat of the
        // existing
        // index and no update occurs.
        if (element == range.getEnd()) {
            return;
        }

        if (range.getEnd() == -1 || (element == (range.getEnd() + range.getIncrementSize()))) {
            // Otherwise if the element is one more than the end of the range, we update the range
            range.increment();
        } else {
            // If it's anything more than one an error must have occurred
            throw new SequenceTypeException("Incorrectly ordered index in update");
        }
    }

    @Override
    public OrderProvider createOrderProvider() {
        return index -> range.indexToNumberPeriodsInt(Integer.valueOf(index));
    }

    public void setEnd(int end) {
        this.range.setEnd(end);
    }

    @Override
    public int getNumberElements() {
        return range.getNumberElements();
    }

    @Override
    public void assignMaximumIndex(int index) {
        // NOTHING TO DO, the maximum is not changed for this sequence-type
    }

    @Override
    public IncompleteElementRange elementRange() {
        return range;
    }
}
