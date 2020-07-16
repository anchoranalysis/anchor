/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer.max;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MaxIntensityBufferFloat extends MaxIntensityBuffer<FloatBuffer> {

    public MaxIntensityBufferFloat(Extent srcExtent) {
        super(srcExtent, VoxelBoxFactory.getFloat());
    }

    @Override
    protected void addBuffer(FloatBuffer pixels, FloatBuffer flatBuffer) {
        float inPixel = pixels.get();
        float flatPixel = flatBuffer.get();
        if (inPixel > flatPixel) {
            flatBuffer.put(flatBuffer.position() - 1, inPixel);
        }
    }
}
