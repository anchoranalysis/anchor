/* (C)2020 */
package org.anchoranalysis.image.object.intersecting;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;

public abstract class CountIntersectingVoxels {

    /**
     * Calculates the number of intersecting pixels between two voxel boxes
     *
     * @param src
     * @param other
     * @param onMaskThis a region membership mask or a binary-high value depending on
     *     region-membership
     * @param onMaskOther a region membership mask or a binary-high value depending on
     *     region-membership
     * @return
     */
    public int countIntersectingVoxels(
            BoundedVoxelBox<ByteBuffer> src, BoundedVoxelBox<ByteBuffer> other) {
        // Find the common bounding box
        Optional<BoundingBox> bboxIntersect =
                src.getBoundingBox().intersection().with(other.getBoundingBox());

        if (!bboxIntersect.isPresent()) {
            // If the bounding boxes don't intersect then we can
            //   go home early
            return 0;
        }

        return countIntersectingVoxelsFromBBox(src, other, bboxIntersect.get());
    }

    // count intersecting pixels
    private int countIntersectingVoxelsFromBBox(
            BoundedVoxelBox<ByteBuffer> src,
            BoundedVoxelBox<ByteBuffer> other,
            BoundingBox bboxIntersect) {
        IntersectionBBox bbox =
                IntersectionBBox.create(
                        src.getBoundingBox(), other.getBoundingBox(), bboxIntersect);

        // Otherwise we count the number of pixels that are not empty
        //  in both voxel boxes in the intersecting region
        int cnt = 0;
        for (int z = bbox.z().min(); z < bbox.z().max(); z++) {

            ByteBuffer buffer = src.getVoxelBox().getPixelsForPlane(z).buffer();

            int zOther = z + bbox.z().rel();
            ByteBuffer bufferOther = other.getVoxelBox().getPixelsForPlane(zOther).buffer();

            cnt += countIntersectingVoxels(buffer, bufferOther, bbox);
        }

        return cnt;
    }

    protected abstract int countIntersectingVoxels(
            ByteBuffer buffer1, ByteBuffer buffer2, IntersectionBBox bbox);
}
