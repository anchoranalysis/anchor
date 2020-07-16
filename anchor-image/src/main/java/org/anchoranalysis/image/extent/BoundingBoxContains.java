/* (C)2020 */
package org.anchoranalysis.image.extent;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;

/**
 * Does a bounding box contain other objects? e.g. points, other bounding boxes etc.
 *
 * @author Owen Feehan
 */
public final class BoundingBoxContains {

    private final BoundingBox bbox;
    private final ReadableTuple3i cornerMax;

    public BoundingBoxContains(BoundingBox bbox) {
        super();
        this.bbox = bbox;
        this.cornerMax = bbox.calcCornerMax();
    }

    /** Is this value in the x-dimension within the bounding box range? */
    public boolean x(int x) {
        return (x >= bbox.cornerMin().getX()) && (x <= cornerMax.getX());
    }

    /** Is this value in the y-dimension within the bounding box range? */
    public boolean y(int y) {
        return (y >= bbox.cornerMin().getY()) && (y <= cornerMax.getY());
    }

    /** Is this value in the z-dimension within the bounding box range? */
    public boolean z(int z) {
        return (z >= bbox.cornerMin().getZ()) && (z <= cornerMax.getZ());
    }

    /** Is this point within the bounding-box? */
    public boolean point(ReadableTuple3i point) {
        return x(point.getX()) && y(point.getY()) && z(point.getZ());
    }

    /** Is this point within the bounding-box, but ignoring the z-dimension? */
    public boolean pointIgnoreZ(Point3i point) {
        return x(point.getX()) && y(point.getY());
    }

    /** Is this other bounding-box FULLY contained within this bounding box? */
    public boolean box(BoundingBox maybeContainedInside) {
        return point(maybeContainedInside.cornerMin())
                && point(maybeContainedInside.calcCornerMax());
    }
}
