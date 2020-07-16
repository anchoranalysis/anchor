/* (C)2020 */
package org.anchoranalysis.image.voxel.box.pixelsforplane;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;

public class PixelsFromFloatBufferArr implements PixelsForPlane<FloatBuffer> {

    private final VoxelBuffer<FloatBuffer>[] buffer;
    private final Extent extent;

    private PixelsFromFloatBufferArr(Extent extent) {
        assert (extent.getZ() > 0);

        this.extent = extent;

        buffer = new VoxelBufferFloat[extent.getZ()];
    }

    private void init() {
        int volumeXY = extent.getVolumeXY();
        for (int z = 0; z < extent.getZ(); z++) {
            buffer[z] = VoxelBufferFloat.allocate(volumeXY);
        }
    }

    // START FACTORY METHODS
    public static PixelsFromFloatBufferArr createInitialised(Extent extent) {
        PixelsFromFloatBufferArr p = new PixelsFromFloatBufferArr(extent);
        p.init();
        return p;
    }

    public static PixelsFromFloatBufferArr createEmpty(Extent extent) {
        return new PixelsFromFloatBufferArr(extent);
    }
    // END FACTORY METHODS

    @Override
    public void setPixelsForPlane(int z, VoxelBuffer<FloatBuffer> pixels) {
        pixels.buffer().clear();
        buffer[z] = pixels;
    }

    @Override
    public VoxelBuffer<FloatBuffer> getPixelsForPlane(int z) {
        VoxelBuffer<FloatBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
