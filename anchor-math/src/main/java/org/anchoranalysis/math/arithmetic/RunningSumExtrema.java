/*-
 * #%L
 * anchor-plugin-annotation
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

package org.anchoranalysis.math.arithmetic;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Like a {@link RunningSum} but also remembers the <i>mean</i> and <i>max</i> across all the added values.
 * 
 * @author Owen Feehan
 *
 */
@Accessors(fluent=true)
public class RunningSumExtrema {

    private RunningSum running = new RunningSum();
    
    /** The minimum across all {@link value}s passed to {@link #add} or {@link Double#MAX_VALUE} if the method was never called. */
    @Getter private double min = Double.MAX_VALUE;

    /** The maximum across all {@link value}s passed to {@link #add} or {@link Double#MIN_VALUE} if the method was never called. */
    @Getter private double max = Double.MIN_VALUE;
    
    /**
     * Adds a value to the running-sum, also remembering if {@code value} is the minimum or maximum of calls to this method.
     * 
     * @param value the value.
     */
    public void add(double value) {
        if (!Double.isNaN(value)) {
            running.increment(value);
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
    }

    /**
     * Calculates the mean.
     *
     * @return the mean or {@code NaN} if {@link #add} was never called.
     */
    public double mean() {
        return running.mean();
    }
}
