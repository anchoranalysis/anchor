/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Assigns an arithmetic sequence of doubles, derived by diving an integer sequence by a divider.
 *
 * <p>Each element to be assigned corresponds to the respective element in the integer-sequence, but
 * divided by {@code divisor}.
 *
 * @author Owen Feehan
 */
public class PermutePropertySequenceDouble extends PermutePropertySequence<Double> {

    // START BEAN PROPERTIES
    /** What to divide the integer-sequence by. */
    @BeanField @Getter @Setter private double divisor = 1.0;

    // END BEAN PROPERTIES

    @Override
    public Iterator<Double> propertyValues() {
        return new DoubleRange(sequenceIterator());
    }

    @Override
    public String describePropertyValue(Double value) {
        return value.toString();
    }

    /** The range of {@link Double} values that can be assigned. */
    class DoubleRange implements Iterator<Double> {

        private Iterator<Integer> itr;

        public DoubleRange(Iterator<Integer> integerSequence) {
            itr = integerSequence;
        }

        @Override
        public boolean hasNext() {
            return itr.hasNext();
        }

        @Override
        public Double next() {
            return itr.next() / divisor;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
