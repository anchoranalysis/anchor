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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Calculates variance efficiently, as successive values are added, using {@code long} to store sums
 * internally.
 *
 * <p>This is similar to {@link VarianceCalculatorDouble} but is designed for {@code int} values
 * rather than {@code double}s.
 *
 * <p>Efficiency occurs by maintaining a running sum, sum-of-squares and count as values are added.
 *
 * <p>This is useful for calculating variance in sequential order in an <a
 * href="https://en.wikipedia.org/wiki/Online_machine_learning">online</a> manner.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@NoArgsConstructor
public class VarianceCalculatorLong {

    /** The running sum of values. */
    private long sum = 0;

    /** The running sum of squares of values. */
    private long sumSquares = 0;

    /** The running count of values. */
    @Getter private long count = 0;

    /**
     * Adds a value to the running sum.
     *
     * @param value the value to add.
     */
    public void add(int value) {
        long valueLong = value;
        this.sum += valueLong;
        this.sumSquares += (valueLong * valueLong);
        this.count++;
    }

    /**
     * Adds a multiple instances of a value to the running sum.
     *
     * @param value the value to add.
     * @param instances how many instances of {@code value} to add.
     */
    public void add(int value, int instances) {
        // A long is used to avoid hitting maximum value.
        long addSum = ((long) instances) * value;
        long addSumSquares = addSum * value;
        assert (addSum >= 0);
        assert (addSumSquares >= 0);

        this.sum += addSum;
        this.sumSquares += addSumSquares;
        this.count += instances;
    }

    /**
     * Subtracts the running-sums and count from another {@link VarianceCalculatorLong} from the
     * current object's state.
     *
     * <p>This occurs immutably, and the currently object's state is unaffected.
     *
     * @param toSubtract the other {@link VarianceCalculatorLong} whose state is subtracted.
     * @return a newly created {@link VarianceCalculatorLong} whose state is the current object
     *     minus {@code toSubtract}.
     */
    public VarianceCalculatorLong subtract(VarianceCalculatorLong toSubtract) {
        return new VarianceCalculatorLong(
                this.sum - toSubtract.sum,
                this.sumSquares - toSubtract.sumSquares,
                this.count - toSubtract.count);
    }

    /**
     * Calculate the <b>mean</b>, based on the current state of running-sums.
     *
     * @return the mean.
     */
    public double mean() {
        return ((double) sum) / count;
    }

    /**
     * Calculate the <b>variance</b>, based on the current state of running-sums.
     *
     * @return the variance.
     */
    public double variance() {
        return VarianceCalculatorHelper.calculateVariance(sum, sumSquares, count);
    }
}
