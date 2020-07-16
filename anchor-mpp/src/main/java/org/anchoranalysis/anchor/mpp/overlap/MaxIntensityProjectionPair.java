/* (C)2020 */
package org.anchoranalysis.anchor.mpp.overlap;

import java.nio.ByteBuffer;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MaxIntensityProjectionPair {

    private final BoundedVoxelBox<ByteBuffer> bufferMIP1;
    private final BoundedVoxelBox<ByteBuffer> bufferMIP2;

    public MaxIntensityProjectionPair(
            BoundedVoxelBox<ByteBuffer> buffer1,
            BoundedVoxelBox<ByteBuffer> buffer2,
            RegionMembershipWithFlags rmFlags1,
            RegionMembershipWithFlags rmFlags2) {
        bufferMIP1 = intensityProjectionFor(buffer1, rmFlags1);
        bufferMIP2 = intensityProjectionFor(buffer2, rmFlags2);
    }

    public int countIntersectingVoxels() {
        // Relies on the binary voxel buffer ON being 255
        return new CountIntersectingVoxelsRegionMembership((byte) 1)
                .countIntersectingVoxels(bufferMIP1, bufferMIP2);
    }

    public int minArea() {
        int cnt1 = bufferMIP1.getVoxelBox().countEqual(BinaryValues.getDefault().getOnInt());
        int cnt2 = bufferMIP2.getVoxelBox().countEqual(BinaryValues.getDefault().getOnInt());
        return Math.min(cnt1, cnt2);
    }

    private static BinaryVoxelBox<ByteBuffer> createBinaryVoxelBoxForFlag(
            VoxelBox<ByteBuffer> vb, RegionMembershipWithFlags rmFlags) {

        VoxelBox<ByteBuffer> vbOut = VoxelBoxFactory.getByte().create(vb.extent());

        BinaryValuesByte bvb = BinaryValuesByte.getDefault();

        for (int z = 0; z < vb.extent().getZ(); z++) {

            ByteBuffer bb = vb.getPixelsForPlane(z).buffer();
            ByteBuffer bbOut = vbOut.getPixelsForPlane(z).buffer();

            int offset = 0;
            for (int y = 0; y < vb.extent().getY(); y++) {
                for (int x = 0; x < vb.extent().getX(); x++) {
                    maybeOutputByte(offset++, bb, bbOut, bvb, rmFlags);
                }
            }
        }

        return new BinaryVoxelBoxByte(vbOut, bvb.createInt());
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

    private static BoundedVoxelBox<ByteBuffer> intensityProjectionFor(
            BoundedVoxelBox<ByteBuffer> buffer, RegionMembershipWithFlags rmFlags) {
        BinaryVoxelBox<ByteBuffer> bvb = createBinaryVoxelBoxForFlag(buffer.getVoxelBox(), rmFlags);

        BoundedVoxelBox<ByteBuffer> bvbBounded =
                new BoundedVoxelBox<>(buffer.getBoundingBox(), bvb.getVoxelBox());

        return bvbBounded.createMaxIntensityProjection();
    }
}
