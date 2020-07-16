/* (C)2020 */
package org.anchoranalysis.image.extent;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;

/**
 * Performs union of a bounding-box with other entities
 *
 * @author Owen Feehan
 */
public class BoundingBoxUnion {

    private final BoundingBox bbox;

    public BoundingBoxUnion(BoundingBox bbox) {
        super();
        this.bbox = bbox;
    }

    /**
     * Performs a union with another box (immutably)
     *
     * @param other the other bounding box
     * @return a new bounding-box that is union of both bounding boxes
     */
    public BoundingBox with(BoundingBox other) {

        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMinOther = other.cornerMin();

        ReadableTuple3i cornerMax = bbox.calcCornerMax();
        ReadableTuple3i cornerMaxOthr = other.calcCornerMax();

        ExtentBoundsComparer meiX =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::getX);
        ExtentBoundsComparer meiY =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::getY);
        ExtentBoundsComparer meiZ =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::getZ);

        return new BoundingBox(
                new Point3i(meiX.getMin(), meiY.getMin(), meiZ.getMin()),
                new Extent(meiX.getExtent(), meiY.getExtent(), meiZ.getExtent()));
    }
}
