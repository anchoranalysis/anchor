/* (C)2020 */
package org.anchoranalysis.image.binary.logical;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryChnlXor {

    public static void apply(Mask chnlCrnt, Mask chnlReceiver) {
        apply(
                chnlCrnt.getVoxelBox(),
                chnlReceiver.getVoxelBox(),
                chnlCrnt.getBinaryValues().createByte(),
                chnlReceiver.getBinaryValues().createByte());
    }

    public static void apply(
            VoxelBox<ByteBuffer> voxelBoxCrnt,
            VoxelBox<ByteBuffer> voxelBoxReceiver,
            BinaryValuesByte bvbCrnt,
            BinaryValuesByte bvbReceiver) {

        Extent e = voxelBoxCrnt.extent();

        // All the on voxels in the receive, are put onto crnt
        for (int z = 0; z < e.getZ(); z++) {

            ByteBuffer bufSrc = voxelBoxCrnt.getPixelsForPlane(z).buffer();
            ByteBuffer bufReceive = voxelBoxReceiver.getPixelsForPlane(z).buffer();

            int offset = 0;
            for (int y = 0; y < e.getY(); y++) {
                for (int x = 0; x < e.getX(); x++) {

                    byte byteSrc = bufSrc.get(offset);
                    byte byteRec = bufReceive.get(offset);

                    boolean srcOn = byteSrc == bvbCrnt.getOnByte();
                    boolean recOn = byteRec == bvbReceiver.getOnByte();

                    if (srcOn != recOn) {
                        bufSrc.put(offset, bvbCrnt.getOnByte());
                    } else {
                        bufSrc.put(offset, bvbCrnt.getOffByte());
                    }

                    offset++;
                }
            }
        }
    }
}
