/* (C)2020 */
package org.anchoranalysis.image.extent;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;

/**
 * Methods for checking intersection between a particular bounding-box and others
 *
 * @author Owen Feehan
 */
public final class BoundingBoxIntersection {

    private final BoundingBox bbox;

    public BoundingBoxIntersection(BoundingBox bbox) {
        super();
        this.bbox = bbox;
    }

    /** Does this bounding box intersect with another? */
    public boolean existsWith(BoundingBox other) {
        return with(other, false).isPresent();
    }

    /** Does this bounding box intersection with any of the others in the list? */
    public boolean existsWithAny(List<BoundingBox> others) {

        for (BoundingBox other : others) {

            if (existsWith(other)) {
                return true;
            }
        }

        return false;
    }

    public Optional<BoundingBox> with(BoundingBox other) {
        return with(other, true);
    }

    /** Find the intersection and clip to a a containing extent */
    public Optional<BoundingBox> withInside(BoundingBox other, Extent containingExtent) {
        return with(other).map(boundingBox -> boundingBox.clipTo(containingExtent));
    }

    /**
     * Determines if the bounding box intersects with another, and optionally creates the
     * bounding-box of intersection
     *
     * @param other the other bounding-box to check intersection with
     * @param createIntersectionBox iff TRUE the bounding-box of the intersection is returned,
     *     otherwise the existing (source) bounding-box is returned
     * @return a bounding-box if there is intersection (which box depends on {@link
     *     createIntersectionBox} or empty() if there is no intersection.
     */
    private Optional<BoundingBox> with(BoundingBox other, boolean createIntersectionBox) {

        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMinOther = other.cornerMin();

        ReadableTuple3i cornerMax = bbox.calcCornerMax();
        ReadableTuple3i cornerMaxOthr = other.calcCornerMax();

        Optional<ExtentBoundsComparer> meiX =
                ExtentBoundsComparer.createMin(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::getX);
        Optional<ExtentBoundsComparer> meiY =
                ExtentBoundsComparer.createMin(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::getY);
        Optional<ExtentBoundsComparer> meiZ =
                ExtentBoundsComparer.createMin(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::getZ);

        if (!meiX.isPresent() || !meiY.isPresent() || !meiZ.isPresent()) {
            return Optional.empty();
        }

        if (createIntersectionBox) {
            return Optional.of(
                    new BoundingBox(
                            new Point3i(
                                    meiX.get().getMin(), meiY.get().getMin(), meiZ.get().getMin()),
                            new Extent(
                                    meiX.get().getExtent(),
                                    meiY.get().getExtent(),
                                    meiZ.get().getExtent())));
        } else {
            return Optional.of(bbox);
        }
    }
}
