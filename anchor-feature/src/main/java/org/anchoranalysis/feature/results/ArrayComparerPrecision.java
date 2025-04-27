/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.results;

import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Precision;

@RequiredArgsConstructor
class ArrayComparerPrecision extends ArrayComparer {

    /** Amount of allowed absolute error. */
    private final double eps;

    @Override
    protected boolean compareItem(Object obj1, Object obj2) {

        // Special case for doubles
        if (obj1 instanceof Double object1Cast) {
            return compareDoubleWithOther(object1Cast, obj2);
        } else {
            return super.compareItem(obj1, obj2);
        }
    }

    private boolean compareDoubleWithOther(Double dbl, Object other) {
        if (other instanceof Double otherCast) {
            return Precision.equals(dbl, otherCast, eps);
        } else {
            return false;
        }
    }
}
