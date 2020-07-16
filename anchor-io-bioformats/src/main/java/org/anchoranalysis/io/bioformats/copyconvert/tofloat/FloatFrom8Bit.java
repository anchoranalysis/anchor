/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tofloat;

import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.ImageDimensions;

public class FloatFrom8Bit extends ConvertToFloat {

    @Override
    protected float[] convertIntegerBytesToFloatArray(
            ImageDimensions sd, byte[] src, int srcOffset) {

        float[] fArr = new float[sd.getX() * sd.getY()];

        int cntLoc = 0;
        for (int y = 0; y < sd.getY(); y++) {
            for (int x = 0; x < sd.getX(); x++) {
                float f = ByteConverter.unsignedByteToInt(src[srcOffset++]);
                fArr[cntLoc++] = f;
            }
        }
        return fArr;
    }

    @Override
    protected int bytesPerPixel() {
        return 1;
    }
}
