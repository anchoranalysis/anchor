/* (C)2020 */
package org.anchoranalysis.image.object.intersecting;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;

/**
 * Determines whether there are any intersecting voxels between binary-masks, exiting as soon as one
 * is encountered (as early as possible)
 *
 * @author Owen Feehan
 */
public class DetermineWhetherIntersectingVoxelsBinary {

    private byte byteOn1;
    private byte byteOn2;

    public DetermineWhetherIntersectingVoxelsBinary(BinaryValuesByte bvb1, BinaryValuesByte bvb2) {
        super();
        this.byteOn1 = bvb1.getOnByte();
        this.byteOn2 = bvb2.getOnByte();
    }

    public boolean hasIntersectingVoxels(
            BoundedVoxelBox<ByteBuffer> src, BoundedVoxelBox<ByteBuffer> other) {
        return pointOfFirstIntersectingVoxel(src, other).isPresent();
    }

    private Optional<Point3i> pointOfFirstIntersectingVoxel(
            BoundedVoxelBox<ByteBuffer> src, BoundedVoxelBox<ByteBuffer> other) {

        // Find the common bounding box
        Optional<BoundingBox> bboxIntersect =
                src.getBoundingBox().intersection().with(other.getBoundingBox());
        return bboxIntersect.flatMap(bbox -> hasIntersectingVoxelsInBoundingBox(src, other, bbox));
    }

    /**
     * @param src
     * @param other
     * @param bboxIntersect
     * @param onMask1
     * @param onMask2
     * @return Point3i if intersection exists, then the first point of intersection found
     *     (newly-created), or else empty if no intersection exists
     */
    private Optional<Point3i> hasIntersectingVoxelsInBoundingBox(
            BoundedVoxelBox<ByteBuffer> src,
            BoundedVoxelBox<ByteBuffer> other,
            BoundingBox bboxIntersect) {

        IntersectionBBox bbox =
                IntersectionBBox.create(
                        src.getBoundingBox(), other.getBoundingBox(), bboxIntersect);

        // Otherwise we count the number of pixels that are not empty
        //  in both voxel boxes in the intersecting region
        for (int z = bbox.z().min(); z < bbox.z().max(); z++) {

            ByteBuffer buffer = src.getVoxelBox().getPixelsForPlane(z).buffer();

            int zOther = z + bbox.z().rel();
            ByteBuffer bufferOther = other.getVoxelBox().getPixelsForPlane(zOther).buffer();

            buffer.clear();
            bufferOther.clear();

            Optional<Point3i> intersectingPoint = hasIntersectingVoxels(buffer, bufferOther, bbox);
            if (intersectingPoint.isPresent()) {
                intersectingPoint.get().setZ(z);
                return intersectingPoint;
            }
        }

        return Optional.empty();
    }

    /**
     * @return Point3i NULL if no intersection exists, otherwise first point of intersection found
     *     (newly-created)
     */
    private Optional<Point3i> hasIntersectingVoxels(
            ByteBuffer buffer1, ByteBuffer buffer2, IntersectionBBox bbox) {

        for (int y = bbox.y().min(); y < bbox.y().max(); y++) {
            int yOther = y + bbox.y().rel();

            for (int x = bbox.x().min(); x < bbox.x().max(); x++) {
                int xOther = x + bbox.x().rel();

                byte posCheck = buffer1.get(bbox.e1().offset(x, y));
                byte posCheckOther = buffer2.get(bbox.e2().offset(xOther, yOther));

                if (posCheck == byteOn1 && posCheckOther == byteOn2) {
                    return Optional.of(new Point3i(x, y, 0));
                }
            }
        }
        return Optional.empty();
    }
}
