/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer.mean;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MeanIntensityByteBuffer extends MeanIntensityBuffer<ByteBuffer> {

    /** Simple constructor since no preprocessing is necessary. */
    public MeanIntensityByteBuffer(Extent srcExtent) {
        super(VoxelBoxFactory.getByte(), srcExtent);
    }

    @Override
    protected void processPixel(ByteBuffer pixels, int index) {
        byte inPixel = pixels.get(index);
        incrSumBuffer(index, ByteConverter.unsignedByteToInt(inPixel));
    }

    @Override
    public void finalizeBuffer() {
        int maxIndex = volumeXY();

        ByteBuffer bbFlat = flatBuffer();
        FloatBuffer bbSum = sumBuffer();
        for (int i = 0; i < maxIndex; i++) {
            bbFlat.put(i, (byte) (bbSum.get(i) / count()));
        }
    }
}
