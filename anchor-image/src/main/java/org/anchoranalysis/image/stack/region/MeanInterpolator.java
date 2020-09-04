/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.stack.region;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.convert.PrimitiveConverter;
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

    public byte getInterpolatedPixelByte(int x0, int y0, UnsignedByteBuffer buffer, Extent extent)
            throws OperationFailedException {

        int sum = 0;
        int cnt = 0;

        for (int y = 0; y < sizeY; y++) {

            int y1 = y0 + y;

            for (int x = 0; x < sizeX; x++) {

                int x1 = x0 + x;

                if (extent.contains(x1, y1, 0)) {
                    int val = PrimitiveConverter.unsignedByteToInt(buffer.get(extent.offset(x0 + x, y1)));
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
                    int val = PrimitiveConverter.unsignedShortToInt(bb.get(e.offset(x0 + x, y1)));
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
