package org.anchoranalysis.math.arithmetic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

/**
 * Utilities for arithmetic operations involving type {@code double}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DoubleUtilities {

    /**
     * Are the two numbers equal?
     *
     * @param value1 the first value.
     * @param value2 the second value.
     * @return true iff {@code value1} and {@code value2} differ by less than {@code
     *     PrecisionConstants.EPSILON}.
     */
    public static boolean areEqual(double value1, double value2) {
        return Math.abs(value1 - value2) < PrecisionConstants.EPSILON;
    }

    /**
     * Replaces a value with a constant if the {@code denominator} is 0.
     *
     * @param numerator numerator.
     * @param denominator denominator.
     * @param replaceWithIfZero what to replace with if zero.
     * @return numerator divided denominator or replaceWithIfZero (if denominator is zero).
     */
    public static double divideByZeroReplace(
            double numerator, double denominator, double replaceWithIfZero) {

        if (areEqual(denominator, 0.0)) {
            return replaceWithIfZero;
        } else {
            return numerator / denominator;
        }
    }
}
