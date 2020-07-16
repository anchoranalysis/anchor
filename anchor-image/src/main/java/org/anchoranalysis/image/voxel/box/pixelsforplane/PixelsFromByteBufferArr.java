/* (C)2020 */
package org.anchoranalysis.image.voxel.box.pixelsforplane;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class PixelsFromByteBufferArr implements PixelsForPlane<ByteBuffer> {

    private final VoxelBuffer<ByteBuffer>[] buffer;
    private final Extent extent;

    private PixelsFromByteBufferArr(Extent extent) {
        assert (extent.getZ() > 0);

        this.extent = extent;

        buffer = new VoxelBufferByte[extent.getZ()];
    }

    private void init() {
        int volumeXY = extent.getVolumeXY();
        for (int z = 0; z < extent.getZ(); z++) {
            buffer[z] = VoxelBufferByte.allocate(volumeXY);
        }
    }

    // START FACTORY METHODS
    public static PixelsFromByteBufferArr createInitialised(Extent extent) {
        PixelsFromByteBufferArr p = new PixelsFromByteBufferArr(extent);
        p.init();
        return p;
    }

    public static PixelsFromByteBufferArr createEmpty(Extent extent) {
        return new PixelsFromByteBufferArr(extent);
    }
    // END FACTORY METHODS

    @Override
    public void setPixelsForPlane(int z, VoxelBuffer<ByteBuffer> pixels) {
        buffer[z] = pixels;
        buffer[z].buffer().clear();
    }

    @Override
    public VoxelBuffer<ByteBuffer> getPixelsForPlane(int z) {
        Preconditions.checkArgument(z >= 0);
        VoxelBuffer<ByteBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
