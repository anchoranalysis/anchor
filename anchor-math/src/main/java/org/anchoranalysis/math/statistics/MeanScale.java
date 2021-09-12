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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores the mean and scale for a distribution.
 *
 * <p>The scale is often the standard-deviation.
 *
 * @author Owen Feehan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeanScale {

    /** The mean. */
    private double mean = 0.0;

    /** The scale, typically the standard-deviation. */
    private double scale = 0.0;

    /**
     * Converts a value to a z-score given the parameterization in this object.
     *
     * <p>See <a href="https://en.wikipedia.org/wiki/Standard_score">Standard score on
     * Wikipedia</a>.
     *
     * @param value the value to convert.
     * @return the value converted to a z-score.
     */
    public double zScore(double value) {
        return ZScoreCalculator.calculateZScore(value, mean, scale);
    }
}
