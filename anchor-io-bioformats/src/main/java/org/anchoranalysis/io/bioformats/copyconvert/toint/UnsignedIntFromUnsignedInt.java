/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats.copyconvert.toint;

import java.nio.ByteBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;

public class UnsignedIntFromUnsignedInt extends ToInt {

    private static final int BYTES_PER_PIXEL = 4;

    protected int sizeBytes;

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        super.setupBefore(dimensions, numberChannelsPerArray);
        this.sizeBytes = sizeXY * BYTES_PER_PIXEL;
    }

    @Override
    protected boolean supportsInterleaving() {
        return false;
    }

    @Override
    protected void copyKeepOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedIntBuffer destination) {
        byte[] sourceArray = source.array();
        int indexOut = 0;
        for (int index = 0; index < sizeBytes; index += BYTES_PER_PIXEL) {
            int value = extractInt(sourceArray, index, littleEndian);
            destination.putRaw(indexOut++, value);
        }
    }

    @Override
    protected void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedIntBuffer destination,
            OrientationChange orientationCorrection) {
        byte[] sourceArray = source.array();
        int x = 0;
        int y = 0;

        for (int index = 0; index < sizeBytes; index += BYTES_PER_PIXEL) {
            int value = extractInt(sourceArray, index, littleEndian);

            int indexOut = orientationCorrection.index(x, y, extent);

            destination.putRaw(indexOut, value);

            x++;
            if (x == extent.x()) {
                y++;
                x = 0;
            }
        }
    }

    private int extractInt(byte[] sourceArray, int index, boolean littleEndian) {
        return DataTools.bytesToInt(sourceArray, index, BYTES_PER_PIXEL, littleEndian);
    }
}
