/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer.max;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MaxIntensityBufferByte extends MaxIntensityBuffer<ByteBuffer> {

    public MaxIntensityBufferByte(Extent srcExtent) {
        super(srcExtent, VoxelBoxFactory.getByte());
    }

    @Override
    protected void addBuffer(ByteBuffer pixels, ByteBuffer flatBuffer) {
        byte inPixel = pixels.get();
        byte flatPixel = flatBuffer.get();
        if (ByteConverter.unsignedByteToInt(inPixel) > ByteConverter.unsignedByteToInt(flatPixel)) {
            flatBuffer.put(flatBuffer.position() - 1, inPixel);
        }
    }
}
