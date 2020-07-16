/* (C)2020 */
package org.anchoranalysis.image.interpolator.transfer;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import java.nio.ByteBuffer;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

// Lots of copying bytes, which doesn't make it very efficient
// Doesn't seem to be a way to make a BufferedImage using existing butes without ending up with a
// BufferedImage.CUSTOM_TYPE
//   type which messes up our scaling
public class TransferViaByte implements Transfer {

    private VoxelBox<ByteBuffer> src;
    private VoxelBox<ByteBuffer> trgt;
    private VoxelBuffer<ByteBuffer> buffer;

    private ResampleOp resampleOp;

    public TransferViaByte(VoxelBoxWrapper src, VoxelBoxWrapper trgt) {
        this.src = src.asByte();
        this.trgt = trgt.asByte();

        int trgtX = trgt.any().extent().getX();
        int trgtY = trgt.any().extent().getY();
        assert (trgtX > 0);
        assert (trgtY > 0);
        resampleOp = new ResampleOp(trgtX, trgtY);
        resampleOp.setFilter(ResampleFilters.getBiCubicFilter());
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
        VoxelBuffer<ByteBuffer> bufIn = trgt.getPixelsForPlane(z);
        VoxelBuffer<ByteBuffer> bufOut =
                interpolator.interpolateByte(buffer, bufIn, src.extent(), trgt.extent());
        if (!bufOut.equals(bufIn)) {
            trgt.setPixelsForPlane(z, bufOut);
        }
    }
}
