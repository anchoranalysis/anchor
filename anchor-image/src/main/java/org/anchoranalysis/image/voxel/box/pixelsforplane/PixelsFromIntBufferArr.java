/* (C)2020 */
package org.anchoranalysis.image.voxel.box.pixelsforplane;

import java.nio.IntBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferInt;

public class PixelsFromIntBufferArr implements PixelsForPlane<IntBuffer> {

    private final VoxelBuffer<IntBuffer>[] buffer;
    private final Extent extent;

    private PixelsFromIntBufferArr(Extent extent) {
        assert (extent.getZ() > 0);

        this.extent = extent;

        buffer = new VoxelBufferInt[extent.getZ()];
    }

    private void init() {
        int volumeXY = extent.getVolumeXY();
        for (int z = 0; z < extent.getZ(); z++) {
            buffer[z] = VoxelBufferInt.allocate(volumeXY);
        }
    }

    // START FACTORY METHODS
    public static PixelsFromIntBufferArr createInitialised(Extent extent) {
        PixelsFromIntBufferArr p = new PixelsFromIntBufferArr(extent);
        p.init();
        return p;
    }

    public static PixelsFromIntBufferArr createEmpty(Extent extent) {
        return new PixelsFromIntBufferArr(extent);
    }
    // END FACTORY METHODS

    @Override
    public void setPixelsForPlane(int z, VoxelBuffer<IntBuffer> pixels) {
        pixels.buffer().clear();
        buffer[z] = pixels;
    }

    @Override
    public VoxelBuffer<IntBuffer> getPixelsForPlane(int z) {
        VoxelBuffer<IntBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
