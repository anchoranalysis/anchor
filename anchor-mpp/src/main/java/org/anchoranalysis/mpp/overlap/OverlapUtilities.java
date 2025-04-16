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
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

/**
 * Utility class for calculating overlap between voxelized marks.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OverlapUtilities {

    /**
     * Calculates the overlap between two voxelized marks for a specific region.
     *
     * @param memo1 the first voxelized mark memo
     * @param memo2 the second voxelized mark memo
     * @param regionID the ID of the region to consider for overlap
     * @return the number of overlapping voxels
     */
    public static double overlapWith(VoxelizedMarkMemo memo1, VoxelizedMarkMemo memo2, int regionID) {

        Mark mark1 = memo1.getMark();
        Mark mark2 = memo2.getMark();

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
                .count(memo1.voxelized().voxels(), memo2.voxelized().voxels());
    }

    /**
     * Counts the number of overlapping voxels between two {@link VoxelizedMarkMemo} objects,
     * considering a global mask.
     *
     * @param memo1 the first voxelized mark memo
     * @param memo2 the second voxelized mark memo
     * @param regionID the ID of the region to consider for overlap
     * @param globalMask the global mask to apply during overlap calculation
     * @param onGlobalMask the value in the global mask that indicates a voxel should be considered
     * @return the total number of overlapping voxels
     */
    public static double overlapWithMaskGlobal(
            VoxelizedMarkMemo memo1,
            VoxelizedMarkMemo memo2,
            int regionID,
            Voxels<UnsignedByteBuffer> globalMask,
            byte onGlobalMask) {
        Mark mark1 = memo1.getMark();
        Mark mark2 = memo2.getMark();

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
                        memo1.voxelized().voxels(),
                        memo2.voxelized().voxels(),
                        globalMask,
                        onGlobalMask);
    }
}