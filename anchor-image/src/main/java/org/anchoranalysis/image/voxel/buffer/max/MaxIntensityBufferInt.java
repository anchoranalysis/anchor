/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer.max;

import java.nio.IntBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MaxIntensityBufferInt extends MaxIntensityBuffer<IntBuffer> {

    public MaxIntensityBufferInt(Extent srcExtent) {
        super(srcExtent, VoxelBoxFactory.getInt());
    }

    @Override
    protected void addBuffer(IntBuffer pixels, IntBuffer flatBuffer) {
        int inPixel = pixels.get();
        int flatPixel = flatBuffer.get();
        if (inPixel > flatPixel) {
            flatBuffer.put(flatBuffer.position() - 1, inPixel);
        }
    }
}
