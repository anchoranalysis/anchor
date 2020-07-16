/* (C)2020 */
package org.anchoranalysis.image.stack.region;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;

class MeanInterpolator {

    private static final String EXC_ZERO_CNT =
            "\"The interpolator has a count of 0, and cannot return a valid value\"";

    private int sizeX;
    private int sizeY;

    public MeanInterpolator(double zoomFactor) {
        double zoomFactorInv = 1 / zoomFactor;
        int size = (int) Math.round(zoomFactorInv);

        sizeX = size;
        sizeY = size;
    }

    public byte getInterpolatedPixelByte(int x0, int y0, ByteBuffer bb, Extent e)
            throws OperationFailedException {

        int sum = 0;
        int cnt = 0;

        for (int y = 0; y < sizeY; y++) {

            int y1 = y0 + y;

            for (int x = 0; x < sizeX; x++) {

                int x1 = x0 + x;

                if (e.contains(x1, y1, 0)) {
                    int val = ByteConverter.unsignedByteToInt(bb.get(e.offset(x0 + x, y1)));
                    sum += val;

                    cnt++;
                }
            }
        }

        if (cnt == 0) {
            throw new OperationFailedException(EXC_ZERO_CNT);
        }

        return (byte) (sum / cnt);
    }

    public short getInterpolatedPixelShort(int x0, int y0, ShortBuffer bb, Extent e)
            throws OperationFailedException {

        int sum = 0;
        int cnt = 0;

        for (int y = 0; y < sizeY; y++) {

            int y1 = y0 + y;

            for (int x = 0; x < sizeX; x++) {

                int x1 = x0 + x;

                if (e.contains(x1, y1, 0)) {
                    int val = ByteConverter.unsignedShortToInt(bb.get(e.offset(x0 + x, y1)));
                    sum += val;

                    cnt++;
                }
            }
        }

        if (cnt == 0) {
            throw new OperationFailedException(EXC_ZERO_CNT);
        }

        return (short) (sum / cnt);
    }
}
