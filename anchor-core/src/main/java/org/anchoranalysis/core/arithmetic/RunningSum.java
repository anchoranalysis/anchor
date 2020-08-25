package org.anchoranalysis.core.arithmetic;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import java.io.Serializable;
import lombok.Getter;

/**
 * Mutable class that allows for incrementing jointly sum and count variables, so as to eventually
 * calculate the mean.
 *
 * <p>It can also be reset to zero, as needed..
 *
 * @author Owen Feehan
 */
public class RunningSum implements Serializable {

    /** */
    private static final long serialVersionUID = -2147459521030056604L;

    /** The running sum */
    @Getter private double sum = 0;

    /** The running count */
    @Getter private int count = 0;

    /**
     * Calculates the mean
     *
     * @return the mean or {@code NaN} if the count is zero.
     */
    public double mean() {
        return mean(Double.NaN);
    }

    /**
     * Calculates the mean
     *
     * @param valueIfCountZero value to use if the count is zero
     * @return the mean or {@code valueIfCountZero} if the count is zero.
     */
    public double mean(double valueIfCountZero) {
        if (count != 0) {
            return sum / count;
        } else {
            return valueIfCountZero;
        }
    }

    /** Calculate the mean and then reset to zero */
    public double meanAndReset() {
        double res = mean();
        reset();
        return res;
    }

    /** Reset the sum and count to zero */
    public void reset() {
        sum = 0.0;
        count = 0;
    }

    /**
     * Adds a new item to the sum, and increments the count by 1.
     *
     * @param sumIncrement the value to add to sum
     */
    public void increment(double sumIncrement) {
        sum += sumIncrement;
        count++;
    }

    /**
     * Increments both the sum and count by particular values
     *
     * @param sumIncrement increment-value for sum
     * @param countIncrement increment-value for count
     */
    public void increment(double sumIncrement, int countIncrement) {
        sum += sumIncrement;
        count += countIncrement;
    }

    /**
     * Increments (adds) an existing {@link RunningSum} to the current.
     *
     * @param runningSum the existing sum, which is unmodified.
     */
    public void increment(RunningSum runningSum) {
        sum += runningSum.getSum();
        count += runningSum.getCount();
    }

    /**
     * Deep-copy
     *
     * @return a new object with new state
     */
    public RunningSum duplicate() {
        RunningSum out = new RunningSum();
        out.sum = sum;
        out.count = count;
        return out;
    }
}
