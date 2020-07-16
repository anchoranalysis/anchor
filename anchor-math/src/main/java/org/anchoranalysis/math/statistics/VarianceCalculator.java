/*-
 * #%L
 * anchor-math
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

package org.anchoranalysis.math.statistics;

/**
 * Helper class for calculating variance from running variables
 *
 * <p>It bundles a sum, sumSquares and count class together
 *
 * @author Owen Feehan
 */
public class VarianceCalculator {

    private long sum = 0;
    private long sumSquares = 0;
    private long count = 0;

    public VarianceCalculator(long sum, long sumSquares, long count) {
        super();
        this.sum = sum;
        this.sumSquares = sumSquares;
        this.count = count;
    }

    public void add(int histCount, long k) {
        // Longs to avoid hitting maximum value
        long addSum = ((long) histCount) * k;
        long addSumSquares = addSum * k;
        assert (addSum >= 0);
        assert (addSumSquares >= 0);

        sum += addSum;
        sumSquares += addSumSquares;
        count += histCount;
    }

    // If all variables are greater-equal than there corresponding variables in other
    public boolean greaterEqualThan(VarianceCalculator other) {
        return (sum >= other.sum) && (sumSquares >= other.sumSquares) && (count >= other.count);
    }

    public VarianceCalculator subtract(VarianceCalculator other) {
        return new VarianceCalculator(
                this.sum - other.sum, this.sumSquares - other.sumSquares, this.count - other.count);
    }

    /**
     * Calculate the variance
     *
     * @return
     */
    public double variance() {
        assert (sumSquares >= 0);
        assert (sum >= 0);
        assert (count >= 0);

        // Formula for variance
        // https://en.wikipedia.org/wiki/Variance
        // https://www.sciencebuddies.org/science-fair-projects/science-fair/variance-and-standard-deviation
        double second = (Math.pow(sum, 2.0)) / count;
        double val = ((double) sumSquares - second) / count;

        assert (val >= 0);
        return val;
    }

    public long getCount() {
        return count;
    }
}
