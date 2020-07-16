/* (C)2020 */
package org.anchoranalysis.image.scale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScaleFactorUtilities {

    /**
     * Calculates a scaling factor so as to scale sdSource to sdTarget
     *
     * <p>i.e. the scale-factor is target/source for each XY dimension
     *
     * @param source source extent
     * @param target target extent
     * @return the scaling-factor to scale the source to be the same size as the target
     */
    public static ScaleFactor calcRelativeScale(Extent source, Extent target) {
        return new ScaleFactor(
                deriveScalingFactor(target.getX(), source.getX()),
                deriveScalingFactor(target.getY(), source.getY()));
    }

    /** Scales a point in XY (immutably) */
    public static Point3i scale(ScaleFactor scalingFactor, Point3i point) {
        return new Point3i(
                ScaleFactorUtilities.scaleQuantity(scalingFactor.getX(), point.getX()),
                ScaleFactorUtilities.scaleQuantity(scalingFactor.getY(), point.getY()),
                point.getZ());
    }

    /**
     * Multiplies a quantity (integer) by a scaling-factor, returning it as an integer
     *
     * <p>Refuses to return 0 or any negative value, making 1 the minimum return value.
     *
     * @param scalingFactor the scaling-factor
     * @param quantity the quantity
     * @return the scaled-quantity, floored to an integer
     */
    public static int scaleQuantity(double scalingFactor, int quantity) {
        int val = (int) (scalingFactor * quantity);
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
    private static double deriveScalingFactor(int numerator, int denominator) {
        return ((double) numerator) / denominator;
    }
}
