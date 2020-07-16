/* (C)2020 */
package org.anchoranalysis.image.binary.logical;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryChnlOr {

    /**
     * A binary OR of chnlCrnt and chnlReceiver where chnlReceiver is overwritten with the output
     *
     * @param chnlCrnt first-channel for OR
     * @param chnlReceiver second-channel for OR (and the channel where the result is overwritten)
     */
    public static void binaryOr(Mask chnlCrnt, Mask chnlReceiver) {

        BinaryValuesByte bvbCrnt = chnlCrnt.getBinaryValues().createByte();
        BinaryValuesByte bvbReceiver = chnlReceiver.getBinaryValues().createByte();

        Extent e = chnlCrnt.getDimensions().getExtent();

        byte crntOn = bvbCrnt.getOnByte();
        byte receiveOn = bvbReceiver.getOnByte();

        // All the on voxels in the receive, are put onto crnt
        for (int z = 0; z < e.getZ(); z++) {

            ByteBuffer bufSrc = chnlCrnt.getVoxelBox().getPixelsForPlane(z).buffer();
            ByteBuffer bufReceive = chnlReceiver.getVoxelBox().getPixelsForPlane(z).buffer();

            int offset = 0;
            for (int y = 0; y < e.getY(); y++) {
                for (int x = 0; x < e.getX(); x++) {

                    byte byteRec = bufReceive.get(offset);
                    if (byteRec == receiveOn) {
                        bufSrc.put(offset, crntOn);
                    }

                    offset++;
                }
            }
        }
    }
}
