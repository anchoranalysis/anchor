/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tofloat;

import com.google.common.io.LittleEndianDataInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.anchoranalysis.image.extent.ImageDimensions;

public class FloatFrom32Bit extends ConvertToFloat {

    private boolean littleEndian;

    public FloatFrom32Bit(boolean littleEndian) {
        super();
        this.littleEndian = littleEndian;
    }

    @Override
    protected float[] convertIntegerBytesToFloatArray(ImageDimensions sd, byte[] src, int srcOffset)
            throws IOException {

        float[] fArr = new float[sd.getX() * sd.getY()];
        int cntLoc = 0;

        ByteArrayInputStream bis = new ByteArrayInputStream(src);

        if (littleEndian) {

            try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(bis)) {
                for (int y = 0; y < sd.getY(); y++) {
                    for (int x = 0; x < sd.getX(); x++) {
                        fArr[cntLoc++] = dis.readFloat();
                    }
                }
                return fArr;
            }

        } else {

            try (DataInputStream dis = new DataInputStream(bis)) {
                for (int y = 0; y < sd.getY(); y++) {
                    for (int x = 0; x < sd.getX(); x++) {

                        float f = dis.readFloat();
                        fArr[cntLoc++] = f;
                    }
                }
                return fArr;
            }
        }
    }

    @Override
    protected int bytesPerPixel() {
        return 4;
    }
}
