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

import java.nio.ByteBuffer;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;

public class MaxIntensityProjectionPair {

    private final BoundedVoxels<ByteBuffer> voxelsProjected1;
    private final BoundedVoxels<ByteBuffer> voxelsProjected2;

    public MaxIntensityProjectionPair(
            BoundedVoxels<ByteBuffer> voxels1,
            BoundedVoxels<ByteBuffer> voxels2,
            RegionMembershipWithFlags rmFlags1,
            RegionMembershipWithFlags rmFlags2) {
        voxelsProjected1 = intensityProjectionFor(voxels1, rmFlags1);
        voxelsProjected2 = intensityProjectionFor(voxels2, rmFlags2);
    }

    public int countIntersectingVoxels() {
        // Relies on the binary voxel buffer ON being 255
        return new CountIntersectingVoxelsRegionMembership((byte) 1)
                .countIntersectingVoxels(voxelsProjected1, voxelsProjected2);
    }

    public int minArea() {
        int cnt1 =
                voxelsProjected1
                        .extract()
                        .voxelsEqualTo(BinaryValues.getDefault().getOnInt())
                        .count();
        int cnt2 =
                voxelsProjected2
                        .extract()
                        .voxelsEqualTo(BinaryValues.getDefault().getOnInt())
                        .count();
        return Math.min(cnt1, cnt2);
    }

    private static BinaryVoxels<ByteBuffer> createBinaryVoxelsForFlag(
            Voxels<ByteBuffer> voxels, RegionMembershipWithFlags rmFlags) {

        Voxels<ByteBuffer> voxelsOut = VoxelsFactory.getByte().createInitialized(voxels.extent());

        BinaryValuesByte bvb = BinaryValuesByte.getDefault();

        for (int z = 0; z < voxels.extent().z(); z++) {

            ByteBuffer bb = voxels.sliceBuffer(z);
            ByteBuffer bbOut = voxelsOut.sliceBuffer(z);

            int offset = 0;
            for (int y = 0; y < voxels.extent().y(); y++) {
                for (int x = 0; x < voxels.extent().x(); x++) {
                    maybeOutputByte(offset++, bb, bbOut, bvb, rmFlags);
                }
            }
        }

        return BinaryVoxelsFactory.reuseByte(voxelsOut, bvb.createInt());
    }

    private static void maybeOutputByte(
            int offset,
            ByteBuffer bb,
            ByteBuffer bbOut,
            BinaryValuesByte bvb,
            RegionMembershipWithFlags rmFlags) {
        byte b = bb.get(offset);
        if (rmFlags.isMemberFlag(b)) {
            bbOut.put(offset, bvb.getOnByte());
        } else {
            if (bvb.getOffByte() != 0) {
                bbOut.put(offset, bvb.getOffByte());
            }
        }
    }

    private static BoundedVoxels<ByteBuffer> intensityProjectionFor(
            BoundedVoxels<ByteBuffer> voxels, RegionMembershipWithFlags rmFlags) {
        BinaryVoxels<ByteBuffer> voxelsBinary = createBinaryVoxelsForFlag(voxels.voxels(), rmFlags);

        BoundedVoxels<ByteBuffer> voxelsBounded =
                new BoundedVoxels<>(voxels.boundingBox(), voxelsBinary.voxels());

        return voxelsBounded.projectMax();
    }
}
