/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.spatial.scale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.point.Point3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScaleFactorUtilities {

    /** Scales a point in XY (immutably) */
    public static Point3i scale(ScaleFactor scalingFactor, Point3i point) {
        return new Point3i(
                ScaleFactorUtilities.scaleQuantity(scalingFactor.x(), point.x()),
                ScaleFactorUtilities.scaleQuantity(scalingFactor.y(), point.y()),
                point.z());
    }

    /**
     * Multiplies a quantity (integer) by a scaling-factor, returning it as an integer
     *
     * <p>Refuses to return 0 or any negative value, making 1 the minimum return value.
     *
     * @param scalingFactor the scaling-factor
     * @param quantity the quantity
     * @return the scaled-quantity, rounded up or down an integer
     */
    public static int scaleQuantity(double scalingFactor, int quantity) {
        int val = (int) Math.round(scalingFactor * quantity);
        return Math.max(val, 1);
    }

    /**
     * Calculates a scaling-factor (for one dimension) by doing a floating point division of two
     * integers
     *
     * @param numerator to divide by
     * @param denominator divider
     * @return floating-point result of division
     */
    public static double deriveScalingFactor(int numerator, int denominator) {
        return ((double) numerator) / denominator;
    }
}
