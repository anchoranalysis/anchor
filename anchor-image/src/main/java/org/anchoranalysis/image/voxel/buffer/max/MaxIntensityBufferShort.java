/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer.max;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MaxIntensityBufferShort extends MaxIntensityBuffer<ShortBuffer> {

    public MaxIntensityBufferShort(Extent srcExtent) {
        super(srcExtent, VoxelBoxFactory.getShort());
    }

    @Override
    protected void addBuffer(ShortBuffer pixels, ShortBuffer flatBuffer) {
        short inPixel = pixels.get();
        short flatPixel = flatBuffer.get();
        if (ByteConverter.unsignedShortToInt(inPixel)
                > ByteConverter.unsignedShortToInt(flatPixel)) {
            flatBuffer.put(flatBuffer.position() - 1, inPixel);
        }
    }
}
