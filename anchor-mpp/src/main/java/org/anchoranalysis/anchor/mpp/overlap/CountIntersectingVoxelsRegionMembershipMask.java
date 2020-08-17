/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.anchor.mpp.overlap;

import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.intersecting.IntersectionBBox;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;

/**
 * Counts the number of intersecting-pixels where bytes are encoded as region memberships
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CountIntersectingVoxelsRegionMembershipMask {

    private final byte regionMembershipFlag;

    public int countIntersectingVoxelsMaskGlobal(
            BoundedVoxels<ByteBuffer> src,
            BoundedVoxels<ByteBuffer> other,
            Voxels<ByteBuffer> maskGlobal,
            byte onMaskGlobal) {
        return countCheckIntersection(
                src, other, src.boundingBox(), other.boundingBox(), maskGlobal, onMaskGlobal);
    }

    private int countCheckIntersection(
            BoundedVoxels<ByteBuffer> src,
            BoundedVoxels<ByteBuffer> other,
            BoundingBox srcBox,
            BoundingBox otherBox,
            Voxels<ByteBuffer> maskGlobal,
            byte onMaskGlobal) {

        // Find the common bounding box
        Optional<BoundingBox> boxIntersect = srcBox.intersection().with(otherBox);

        if (!boxIntersect.isPresent()) {
            // If the bounding boxes don't intersect then we can
            //   go home early
            return 0;
        }

        return countIntersectingVoxelsFromBBoxMaskGlobal(
                src, other, boxIntersect.get(), maskGlobal, onMaskGlobal);
    }

    // count intersecting pixels, but only includes a pixel ifs marked as onMaskGlobal in the mask
    //   voxel buffer
    private int countIntersectingVoxelsFromBBoxMaskGlobal(
            BoundedVoxels<ByteBuffer> src,
            BoundedVoxels<ByteBuffer> other,
            BoundingBox boxIntersect,
            Voxels<ByteBuffer> maskGlobal,
            byte onMaskGlobal) {
        Extent eGlobalMask = maskGlobal.extent();

        IntersectionBBox box =
                IntersectionBBox.create(src.boundingBox(), other.boundingBox(), boxIntersect);

        // Otherwise we count the number of pixels that are not empty
        //  in both bounded-voxels in the intersecting region
        int cnt = 0;

        for (int z = box.z().min(); z < box.z().max(); z++) {

            ByteBuffer buffer = src.voxels().sliceBuffer(z);

            int zOther = z + box.z().rel();
            int zGlobal = z + src.boundingBox().cornerMin().z();

            ByteBuffer bufferOther = other.voxels().sliceBuffer(zOther);
            ByteBuffer bufferMaskGlobal = maskGlobal.sliceBuffer(zGlobal);

            buffer.clear();
            bufferOther.clear();

            cnt +=
                    countIntersectingVoxelsOnGlobalMask(
                            buffer,
                            bufferOther,
                            bufferMaskGlobal,
                            box,
                            src.boundingBox().cornerMin(),
                            eGlobalMask,
                            onMaskGlobal);
        }

        return cnt;
    }

    private int countIntersectingVoxelsOnGlobalMask(
            ByteBuffer buffer1,
            ByteBuffer buffer2,
            ByteBuffer bufferMaskGlobal,
            IntersectionBBox box,
            ReadableTuple3i pointGlobalRel,
            Extent extentGlobal,
            byte onMaskGlobal) {

        int cnt = 0;
        for (int y = box.y().min(); y < box.y().max(); y++) {
            int yOther = y + box.y().rel();
            int yGlobal = y + pointGlobalRel.y();

            for (int x = box.x().min(); x < box.x().max(); x++) {
                int xOther = x + box.x().rel();
                int xGlobal = x + pointGlobalRel.x();

                byte globalMask = bufferMaskGlobal.get(extentGlobal.offset(xGlobal, yGlobal));
                if (globalMask == onMaskGlobal) {

                    byte posCheck = buffer1.get(box.e1().offset(x, y));
                    byte posCheckOther = buffer2.get(box.e2().offset(xOther, yOther));

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
