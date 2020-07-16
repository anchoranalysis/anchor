/* (C)2020 */
package org.anchoranalysis.image.interpolator.transfer;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

// Lots of copying bytes, which doesn't make it very efficient
// Doesn't seem to be a way to make a BufferedImage using existing butes without ending up with a
// BufferedImage.CUSTOM_TYPE
//   type which messes up our scaling
public class TransferViaShort implements Transfer {

    private VoxelBox<ShortBuffer> src;
    private VoxelBox<ShortBuffer> trgt;
    private VoxelBuffer<ShortBuffer> buffer;

    public TransferViaShort(VoxelBoxWrapper src, VoxelBoxWrapper trgt) {
        this.src = src.asShort();
        this.trgt = trgt.asShort();
    }

    @Override
    public void assignSlice(int z) {
        buffer = src.getPixelsForPlane(z);
    }

    @Override
    public void transferCopyTo(int z) {
        trgt.setPixelsForPlane(z, buffer.duplicate());
    }

    @Override
    public void transferTo(int z, Interpolator interpolator) {

        VoxelBuffer<ShortBuffer> bufIn = trgt.getPixelsForPlane(z);
        VoxelBuffer<ShortBuffer> bufOut =
                interpolator.interpolateShort(buffer, bufIn, src.extent(), trgt.extent());
        if (!bufOut.equals(bufIn)) {
            trgt.setPixelsForPlane(z, bufOut);
        }
    }
}
