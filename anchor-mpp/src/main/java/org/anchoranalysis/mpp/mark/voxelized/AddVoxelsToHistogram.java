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

package org.anchoranalysis.mpp.mark.voxelized;

import java.util.List;
import java.util.function.IntConsumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.index.IndexByChannel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class AddVoxelsToHistogram {

    public static void addVoxels(
            byte membership,
            List<RegionMembershipWithFlags> listRegionMembership,
            IndexByChannel<Histogram> partitionList,
            BufferArrayList bufferArrList,
            int globalOffset,
            int zLocal) {
        iterateRegions(
                membership,
                listRegionMembership,
                r -> addRegionToPartition(partitionList, bufferArrList, globalOffset, r, zLocal));
    }

    private static void addRegionToPartition(
            IndexByChannel<Histogram> partitionList,
            BufferArrayList bufferArrList,
            int globalOffset,
            int r,
            int zLocal) {
        for (int i = 0; i < partitionList.size(); i++) {
            partitionList
                    .get(i)
                    .addToVoxelList(r, zLocal, bufferArrList.get(i).getUnsigned(globalOffset));
        }
    }

    private static void iterateRegions(
            byte membership,
            List<RegionMembershipWithFlags> listRegionMembership,
            IntConsumer func) {
        // We optimise this
        for (int r = 0; r < listRegionMembership.size(); r++) {
            RegionMembershipWithFlags rm = listRegionMembership.get(r);
            if (rm.isMemberFlag(membership)) {
                func.accept(r);
            }
        }
    }
}
