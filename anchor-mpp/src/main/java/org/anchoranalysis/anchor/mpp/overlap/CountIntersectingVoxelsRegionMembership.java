/* (C)2020 */
package org.anchoranalysis.anchor.mpp.overlap;

import java.nio.ByteBuffer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.image.object.intersecting.CountIntersectingVoxels;
import org.anchoranalysis.image.object.intersecting.IntersectionBBox;

/**
 * Counts the number of intersecting pixels where each buffer is encoded as region-membership
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CountIntersectingVoxelsRegionMembership extends CountIntersectingVoxels {

    private final byte regionMembershipFlag;

    @Override
    protected int countIntersectingVoxels(
            ByteBuffer buffer1, ByteBuffer buffer2, IntersectionBBox bbox) {

        int cnt = 0;
        for (int y = bbox.y().min(); y < bbox.y().max(); y++) {
            int yOther = y + bbox.y().rel();

            for (int x = bbox.x().min(); x < bbox.x().max(); x++) {
                int xOther = x + bbox.x().rel();

                byte posCheck = buffer1.get(bbox.e1().offset(x, y));
                byte posCheckOther = buffer2.get(bbox.e2().offset(xOther, yOther));

                if (isPixelInRegion(posCheck) && isPixelInRegion(posCheckOther)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    private boolean isPixelInRegion(byte pixelVal) {
        return RegionMembershipUtilities.isMemberFlagAnd(pixelVal, regionMembershipFlag);
    }
}
