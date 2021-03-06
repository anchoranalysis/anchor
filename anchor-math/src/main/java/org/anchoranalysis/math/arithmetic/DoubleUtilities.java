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
 * Utilities for arithmetic operations involving type {@code double}
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DoubleUtilities {

    /** Are the two numbers equal? */
    public static boolean areEqual(double d1, double d2) {
        return Math.abs(d1 - d2) < PrecisionConstants.EPSILON;
    }

    /**
     * Replaces a value with a constant if the divider is 0
     *
     * @param num numerator
     * @param dem denominator
     * @param replaceWithIfZero what to replace with if zero
     * @return numerator divided denominator or replaceWithIfZero (if denominator is zero)
     */
    public static double divideByZeroReplace(double num, double dem, double replaceWithIfZero) {

        if (areEqual(dem, 0.0)) {
            return replaceWithIfZero;
        } else {
            return num / dem;
        }
    }
}
