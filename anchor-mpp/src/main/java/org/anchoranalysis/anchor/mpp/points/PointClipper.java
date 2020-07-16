/* (C)2020 */
package org.anchoranalysis.anchor.mpp.points;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.ImageDimensions;

/** Ensures a point has values contained inside image-dimensions */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointClipper {

    public static Point3i clip(Point3i point, ImageDimensions dimensions) {
        point = clipLow(point);
        point = clipHigh(point, dimensions);
        return point;
    }

    public static Point3d clip(Point3d point, ImageDimensions dimensions) {
        point = clipLow(point);
        point = clipHigh(point, dimensions);
        return point;
    }

    private static Point3i clipLow(Point3i point) {
        return point.max(0);
    }

    private static Point3d clipLow(Point3d point) {
        return point.max(0);
    }

    private static Point3i clipHigh(Point3i point, ImageDimensions dimensions) {
        return point.min(dimensions.getExtent().createMinusOne());
    }

    private static Point3d clipHigh(Point3d point, ImageDimensions dimensions) {
        return point.min(dimensions.getExtent().createMinusOne());
    }
}
