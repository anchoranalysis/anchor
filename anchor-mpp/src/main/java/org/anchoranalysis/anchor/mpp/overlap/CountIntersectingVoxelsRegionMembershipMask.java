/* (C)2020 */
package org.anchoranalysis.anchor.mpp.overlap;

import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.intersecting.IntersectionBBox;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * Counts the number of intersecting-pixels where bytes are encoded as region memberships
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CountIntersectingVoxelsRegionMembershipMask {

    private final byte regionMembershipFlag;

    public int countIntersectingVoxelsMaskGlobal(
            BoundedVoxelBox<ByteBuffer> src,
            BoundedVoxelBox<ByteBuffer> other,
            VoxelBox<ByteBuffer> maskGlobal,
            byte onMaskGlobal) {
        return countCheckIntersection(
                src, other, src.getBoundingBox(), other.getBoundingBox(), maskGlobal, onMaskGlobal);
    }

    private int countCheckIntersection(
            BoundedVoxelBox<ByteBuffer> src,
            BoundedVoxelBox<ByteBuffer> other,
            BoundingBox srcBox,
            BoundingBox otherBox,
            VoxelBox<ByteBuffer> maskGlobal,
            byte onMaskGlobal) {

        // Find the common bounding box
        Optional<BoundingBox> bboxIntersect = srcBox.intersection().with(otherBox);

        if (!bboxIntersect.isPresent()) {
            // If the bounding boxes don't intersect then we can
            //   go home early
            return 0;
        }

        return countIntersectingVoxelsFromBBoxMaskGlobal(
                src, other, bboxIntersect.get(), maskGlobal, onMaskGlobal);
    }

    // count intersecting pixels, but only includes a pixel ifs marked as onMaskGlobal in the mask
    //   voxel buffer
    private int countIntersectingVoxelsFromBBoxMaskGlobal(
            BoundedVoxelBox<ByteBuffer> src,
            BoundedVoxelBox<ByteBuffer> other,
            BoundingBox bboxIntersect,
            VoxelBox<ByteBuffer> maskGlobal,
            byte onMaskGlobal) {
        Extent eGlobalMask = maskGlobal.extent();

        IntersectionBBox bbox =
                IntersectionBBox.create(
                        src.getBoundingBox(), other.getBoundingBox(), bboxIntersect);

        // Otherwise we count the number of pixels that are not empty
        //  in both voxel boxes in the intersecting region
        int cnt = 0;

        for (int z = bbox.z().min(); z < bbox.z().max(); z++) {

            ByteBuffer buffer = src.getVoxelBox().getPixelsForPlane(z).buffer();

            int zOther = z + bbox.z().rel();
            int zGlobal = z + src.getBoundingBox().cornerMin().getZ();

            ByteBuffer bufferOther = other.getVoxelBox().getPixelsForPlane(zOther).buffer();
            ByteBuffer bufferMaskGlobal = maskGlobal.getPixelsForPlane(zGlobal).buffer();

            buffer.clear();
            bufferOther.clear();

            cnt +=
                    countIntersectingVoxelsOnGlobalMask(
                            buffer,
                            bufferOther,
                            bufferMaskGlobal,
                            bbox,
                            src.getBoundingBox().cornerMin(),
                            eGlobalMask,
                            onMaskGlobal);
        }

        return cnt;
    }

    private int countIntersectingVoxelsOnGlobalMask(
            ByteBuffer buffer1,
            ByteBuffer buffer2,
            ByteBuffer bufferMaskGlobal,
            IntersectionBBox bbox,
            Point3i pointGlobalRel,
            Extent extentGlobal,
            byte onMaskGlobal) {

        int cnt = 0;
        for (int y = bbox.y().min(); y < bbox.y().max(); y++) {
            int yOther = y + bbox.y().rel();
            int yGlobal = y + pointGlobalRel.getY();

            for (int x = bbox.x().min(); x < bbox.x().max(); x++) {
                int xOther = x + bbox.x().rel();
                int xGlobal = x + pointGlobalRel.getX();

                byte globalMask = bufferMaskGlobal.get(extentGlobal.offset(xGlobal, yGlobal));
                if (globalMask == onMaskGlobal) {

                    byte posCheck = buffer1.get(bbox.e1().offset(x, y));
                    byte posCheckOther = buffer2.get(bbox.e2().offset(xOther, yOther));

                    if (isPixelInRegion(posCheck) && isPixelInRegion(posCheckOther)) {
                        cnt++;
                    }
                }
            }
        }
        return cnt;
    }

    private boolean isPixelInRegion(byte pixelVal) {
        return RegionMembershipUtilities.isMemberFlagAnd(pixelVal, regionMembershipFlag);
    }
}
