/* (C)2020 */
package org.anchoranalysis.anchor.mpp.overlap;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.image.voxel.box.VoxelBox;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OverlapUtilities {

    public static double overlapWith(VoxelizedMarkMemo pmm1, VoxelizedMarkMemo pmm2, int regionID) {

        Mark mark1 = pmm1.getMark();
        Mark mark2 = pmm2.getMark();

        // If we have a quick routine available, we use this
        // We do some quick tests to get rid of obvious cases which do not overlap
        if (mark1.quickOverlap().isPresent()
                && mark1.quickOverlap().get().noOverlapWith(mark2, regionID)) { // NOSONAR
            return 0.0;
        }

        // Otherwise we do it the slow way by seeing if any pixels intersect
        // between the two bounding box
        byte flag = RegionMembershipUtilities.flagForRegion(regionID);
        return new CountIntersectingVoxelsRegionMembership(flag)
                .countIntersectingVoxels(
                        pmm1.voxelized().getVoxelBox(), pmm2.voxelized().getVoxelBox());
    }

    /**
     * Counts the number of overlapping voxels between two {@link VoxelizedMarkMemo}
     *
     * @param pmm1
     * @param pmm2
     * @param regionID
     * @param globalMask
     * @param onGlobalMask
     * @return the total number of overlapping boxels
     */
    public static double overlapWithMaskGlobal(
            VoxelizedMarkMemo pmm1,
            VoxelizedMarkMemo pmm2,
            int regionID,
            VoxelBox<ByteBuffer> globalMask,
            byte onGlobalMask) {
        Mark mark1 = pmm1.getMark();
        Mark mark2 = pmm2.getMark();

        // If we have a quick routine available, we use this
        // We do some quick tests to get rid of obvious cases which do not overlap
        if (mark1.quickOverlap().isPresent()
                && mark1.quickOverlap().get().noOverlapWith(mark2, regionID)) { // NOSONAR
            return 0.0;
        }

        // Otherwise we do it the slow way by seeing if any pixels intersect between the two
        // bounding box
        byte flag = RegionMembershipUtilities.flagForRegion(regionID);
        return new CountIntersectingVoxelsRegionMembershipMask(flag)
                .countIntersectingVoxelsMaskGlobal(
                        pmm1.voxelized().getVoxelBox(),
                        pmm2.voxelized().getVoxelBox(),
                        globalMask,
                        onGlobalMask);
    }
}
