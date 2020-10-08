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

package org.anchoranalysis.mpp.overlap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

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
        return new CountIntersectingVoxels(flag)
                .count(pmm1.voxelized().voxels(), pmm2.voxelized().voxels());
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
            Voxels<UnsignedByteBuffer> globalMask,
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
        return new CountIntersectingVoxels(flag)
                .countMasked(
                        pmm1.voxelized().voxels(),
                        pmm2.voxelized().voxels(),
                        globalMask,
                        onGlobalMask);
    }
}
