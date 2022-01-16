package org.anchoranalysis.spatial.box;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Utility functions to help with operations to clamp entities to be inside a certain {@link
 * Extent}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ClampToUtilities {

    /**
     * Makes a copy of {@link Point3i} where any dimension whose value equals or exceeds the
     * corresponding value in {@link Extent} is reduced to the maximum permitted.
     *
     * @param point the point to test.
     * @param extent the {@link Extent} in which {@code point} must fully reside.
     * @return a copy of {@link Point3i} where any values greater or equal to the corresponding
     *     value in {@code extent} are reduced to the maximum permitted.
     */
    public static Point3i limitToExtent(ReadableTuple3i point, Extent extent) {
        Point3i copy = new Point3i(point);
        if (copy.x() >= extent.x()) {
            copy.setX(extent.x() - 1);
        }
        if (copy.y() >= extent.y()) {
            copy.setY(extent.y() - 1);
        }
        if (copy.z() >= extent.z()) {
            copy.setZ(extent.z() - 1);
        }
        return copy;
    }

    /**
     * Make a copy of a {@link Point3i} forcing any non-negative dimension values to be 0.
     *
     * @param point the point to test.
     * @return a copy of {@link Point3i} where any negative values are replaced with a 0.
     */
    public static Point3i replaceNegativeWithZero(Point3i point) {
        Point3i copy = new Point3i(point);

        if (copy.x() < 0) {
            copy.setX(0);
        }
        if (copy.y() < 0) {
            copy.setY(0);
        }
        if (copy.z() < 0) {
            copy.setZ(0);
        }
        return copy;
    }

    /**
     * Whether {@code point} non-negative for each dimension?
     *
     * @param point the point to test.
     * @return true iff the point is non-zero for all dimensions.
     */
    public static boolean pointNonZero(ReadableTuple3i point) {
        return point.matchAllDimensions(value -> value >= 0);
    }

    /**
     * Whether {@code point} less than {@code extent} for each dimension?
     *
     * @param point the point to test.
     * @param extent the size that {@code point} must be less than for each corresponding dimension.
     * @return true iff the point is less than {@code extent} for all dimensions.
     */
    public static boolean pointLessThan(ReadableTuple3i point, Extent extent) {
        return point.matchAllDimensions(
                extent.asTuple(), (int value1, int value2) -> value1 < value2);
    }
}