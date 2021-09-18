/*-
 * #%L
 * anchor-math
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.math.statistics;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class VarianceCalculatorHelper {

    /**
     * Calculate the variance, given a sum, running sum and count.
     *
     * @param sum the sum of all values.
     * @param sumSquares the sum of squares of all values.
     * @param count how many separate values exist.
     * @return the variance.
     */
    public static double calculateVariance(double sum, double sumSquares, long count) {
        Preconditions.checkArgument(sumSquares >= 0);
        Preconditions.checkArgument(sum >= 0);
        Preconditions.checkArgument(count >= 0);

        // Formula for variance
        // https://en.wikipedia.org/wiki/Variance
        // https://www.sciencebuddies.org/science-fair-projects/science-fair/variance-and-standard-deviation
        double second = Math.pow(sum, 2.0) / count;
        double val = (sumSquares - second) / count;

        assert (val >= 0);
        return val;
    }
}
