/* (C)2020 */
package org.anchoranalysis.image.voxel.box.pixelsforplane;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;

public class PixelsFromShortBufferArr implements PixelsForPlane<ShortBuffer> {

    private final VoxelBuffer<ShortBuffer>[] buffer;
    private final Extent extent;

    private PixelsFromShortBufferArr(Extent extent) {
        assert (extent.getZ() > 0);

        this.extent = extent;

        buffer = new VoxelBufferShort[extent.getZ()];
    }

    private void init() {
        int volumeXY = extent.getVolumeXY();
        for (int z = 0; z < extent.getZ(); z++) {
            buffer[z] = VoxelBufferShort.allocate(volumeXY);
            assert (buffer[z].buffer().array().length == volumeXY);
        }
    }

    // START FACTORY METHODS
    public static PixelsFromShortBufferArr createInitialised(Extent extent) {
        PixelsFromShortBufferArr p = new PixelsFromShortBufferArr(extent);
        p.init();
        return p;
    }

    public static PixelsFromShortBufferArr createEmpty(Extent extent) {
        return new PixelsFromShortBufferArr(extent);
    }
    // END FACTORY METHODS

    @Override
    public void setPixelsForPlane(int z, VoxelBuffer<ShortBuffer> pixels) {
        pixels.buffer().clear();
        buffer[z] = pixels;
        assert (pixels.buffer().array().length == extent.getVolumeXY());
    }

    @Override
    public VoxelBuffer<ShortBuffer> getPixelsForPlane(int z) {
        VoxelBuffer<ShortBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
