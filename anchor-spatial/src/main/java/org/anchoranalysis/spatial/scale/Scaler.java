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

/**
 * Scales different types of entities with a {@link ScaleFactor}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Scaler {

    /**
     * Scales a point in XY by multiplying each dimension by its corresponding scaling-factor.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * <p>Each dimension is <b>rounded</b> to it's nearest value after scaling.
     *
     * @param scalingFactor the factor to use for scaling.
     * @param point the point to scale.
     * @return a newly created point, where the X and Y dimensions have been scaled by the
     *     corresponding factor in {@code scalingFactor} and the z-dimension value is identical.
     */
    public static Point3i scale(ScaleFactor scalingFactor, Point3i point) {
        return new Point3i(
                Scaler.scaleRound(scalingFactor.x(), point.x()),
                Scaler.scaleRound(scalingFactor.y(), point.y()),
                point.z());
    }

    /**
     * Scales a point in XY by multiplying each dimension by its corresponding scaling-factor.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * <p>Each dimension is <b>scaled</b> to it's nearest value after scaling.
     *
     * @param scalingFactor the factor to use for scaling.
     * @param point the point to scale.
     * @return a newly created point, where the X and Y dimensions have been scaled by the
     *     corresponding factor in {@code scalingFactor} and the z-dimension value is identical.
     */
    public static Point3i scaleCeil(ScaleFactor scalingFactor, Point3i point) {
        return new Point3i(
                Scaler.scaleCeil(scalingFactor.x(), point.x()),
                Scaler.scaleCeil(scalingFactor.y(), point.y()),
                point.z());
    }

    /**
     * Multiplexes between {@link #scaleRound(double, int)} and {@link #scaleCeil}.
     *
     * @param scalingFactor
     * @param quantity
     * @param round if true, {@link #scaleRound(double, int)} is called, otherwise {@link
     *     #scaleCeil(double, int)}.
     * @return the scaled-quantity, rounded or <i>ceil</i>ed to the nearest integer.
     */
    public static int scaleMultiplex(double scalingFactor, int quantity, boolean round) {
        if (round) {
            return scaleRound(scalingFactor, quantity);
        } else {
            return scaleCeil(scalingFactor, quantity);
        }
    }

    /**
     * Multiplies a quantity (integer) by a scaling-factor, returning it as an integer via
     * <b>rounding</b>.
     *
     * <p>Refuses to return 0 or any negative value, making 1 the minimum return value.
     *
     * @param scalingFactor the scaling-factor.
     * @param quantity the quantity.
     * @return the scaled-quantity, rounded up or down an integer.
     */
    public static int scaleRound(double scalingFactor, int quantity) {
        int val = (int) Math.round(scalingFactor * quantity);
        return Math.max(val, 1);
    }

    /**
     * Multiplies a quantity (integer) by a scaling-factor, returning it as an integer via
     * <b>ceil</b>.
     *
     * <p>Refuses to return 0 or any negative value, making 1 the minimum return value.
     *
     * @param scalingFactor the scaling-factor.
     * @param quantity the quantity.
     * @return the scaled-quantity, rounded up or down an integer.
     */
    public static int scaleCeil(double scalingFactor, int quantity) {
        int val = (int) Math.ceil(scalingFactor * quantity);
        return Math.max(val, 1);
    }

    /**
     * Multiplies a quantity (integer) by a scaling-factor, returning it as an integer via
     * <b>floor</b>.
     *
     * <p>Refuses to return 0 or any negative value, making 1 the minimum return value.
     *
     * @param scalingFactor the scaling-factor.
     * @param quantity the quantity.
     * @return the scaled-quantity, rounded up or down an integer.
     */
    public static int scaleFloor(double scalingFactor, int quantity) {
        int val = (int) Math.floor(scalingFactor * quantity);
        return Math.max(val, 1);
    }

    /**
     * Calculates a scaling-factor (for one dimension) by doing a floating point division of two
     * integers.
     *
     * @param numerator to divide by.
     * @param denominator divider.
     * @return floating-point result of division.
     */
    public static double deriveScalingFactor(int numerator, int denominator) {
        return ((double) numerator) / denominator;
    }
}
