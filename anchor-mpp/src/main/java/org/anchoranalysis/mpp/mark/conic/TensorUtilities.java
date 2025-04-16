/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark.conic;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for tensor-related operations and array/matrix creation.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TensorUtilities {

    /**
     * Calculates the square of a given value.
     *
     * @param val the value to square
     * @return the squared value
     */
    public static final double squared(double val) {
        return Math.pow(val, 2);
    }

    /**
     * Creates a two-element array with the second value set to 0.0.
     *
     * @param firstVal the value for the first element
     * @return a two-element array
     */
    public static final double[] twoElementArray(double firstVal) {
        return twoElementArray(firstVal, 0);
    }

    /**
     * Creates a two-element array with specified values.
     *
     * @param firstVal the value for the first element
     * @param secondVal the value for the second element
     * @return a two-element array
     */
    public static final double[] twoElementArray(double firstVal, double secondVal) {
        return new double[] {firstVal, secondVal};
    }

    /**
     * Creates a three-element array with specified values.
     *
     * @param firstVal the value for the first element
     * @param secondVal the value for the second element
     * @param thirdVal the value for the third element
     * @return a three-element array
     */
    public static final double[] threeElementArray(
            double firstVal, double secondVal, double thirdVal) {
        return new double[] {firstVal, secondVal, thirdVal};
    }

    /**
     * Creates a two-element DoubleMatrix1D with specified values.
     *
     * @param x the value for the first element
     * @param y the value for the second element
     * @return a two-element DoubleMatrix1D
     */
    public static final DoubleMatrix1D twoElementMatrix(double x, double y) {
        DoubleMatrix1D out = DoubleFactory1D.dense.make(2);
        out.set(0, x);
        out.set(1, y);
        return out;
    }

    /**
     * Creates a three-element DoubleMatrix1D with specified values.
     *
     * @param x the value for the first element
     * @param y the value for the second element
     * @param z the value for the third element
     * @return a three-element DoubleMatrix1D
     */
    public static final DoubleMatrix1D threeElementMatrix(double x, double y, double z) {
        DoubleMatrix1D out = DoubleFactory1D.dense.make(3);
        out.set(0, x);
        out.set(1, y);
        out.set(2, z);
        return out;
    }
}