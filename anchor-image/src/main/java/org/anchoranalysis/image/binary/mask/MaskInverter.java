/* (C)2020 */
package org.anchoranalysis.image.binary.mask;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskInverter {

    public static ObjectMask invertObjectDuplicate(ObjectMask object) {
        BinaryVoxelBox<ByteBuffer> bvb = object.binaryVoxelBox().duplicate();
        bvb.invert();
        return new ObjectMask(bvb);
    }

    public static void invertChnl(Mask chnl) {

        BinaryValues bv = chnl.getBinaryValues();
        BinaryValuesByte bvb = bv.createByte();
        invertVoxelBox(chnl.getVoxelBox(), bvb);
    }

    public static void invertVoxelBox(VoxelBox<ByteBuffer> vb, BinaryValuesByte bvb) {
        for (int z = 0; z < vb.extent().getZ(); z++) {

            ByteBuffer bb = vb.getPixelsForPlane(z).buffer();

            int offset = 0;
            for (int y = 0; y < vb.extent().getY(); y++) {
                for (int x = 0; x < vb.extent().getX(); x++) {

                    byte val = bb.get(offset);

                    if (val == bvb.getOnByte()) {
                        bb.put(offset, bvb.getOffByte());
                    } else {
                        bb.put(offset, bvb.getOnByte());
                    }

                    offset++;
                }
            }
        }
    }
}
