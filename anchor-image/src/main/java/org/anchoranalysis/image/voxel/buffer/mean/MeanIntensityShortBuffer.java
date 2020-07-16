/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer.mean;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MeanIntensityShortBuffer extends MeanIntensityBuffer<ShortBuffer> {

    /** Simple constructor since no preprocessing is necessary. */
    public MeanIntensityShortBuffer(Extent srcExtent) {
        super(VoxelBoxFactory.getShort(), srcExtent);
    }

    @Override
    protected void processPixel(ShortBuffer pixels, int index) {
        short inPixel = pixels.get(index);
        incrSumBuffer(index, ByteConverter.unsignedShortToInt(inPixel));
    }

    @Override
    public void finalizeBuffer() {
        int maxIndex = volumeXY();

        ShortBuffer bbFlat = flatBuffer();
        FloatBuffer bbSum = sumBuffer();
        for (int i = 0; i < maxIndex; i++) {
            bbFlat.put(i, (byte) (bbSum.get(i) / count()));
        }
    }
}
