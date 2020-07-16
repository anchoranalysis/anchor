/* (C)2020 */
package org.anchoranalysis.image.object.factory;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateFromEntireChnlFactory {

    public static ObjectMask createObject(Mask binaryImgChnl) {
        Channel chnl = binaryImgChnl.getChannel();

        VoxelBox<ByteBuffer> vb = chnl.getVoxelBox().asByte();

        return new ObjectMask(new BoundingBox(vb.extent()), vb, binaryImgChnl.getBinaryValues());
    }
}
