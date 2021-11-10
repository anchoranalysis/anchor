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

package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Converts data of type <i>unsigned short</i> to <i>unsigned byte</i>.
 *
 * <p>If more than 8-bits are being used in the input values, scaling is applied to map the range of
 * effective-bits (how many bits are used) to an 8-bit range.
 *
 * @author Owen Feehan
 */
public class UnsignedByteFromUnsignedShort extends ToUnsignedByteWithScaling {

    /**
     * Create with a number of effective-bits.
     *
     * @param effectiveBits the number of bits that are used in the input-type e.g. 8 or 12 or 16.
     */
    public UnsignedByteFromUnsignedShort(int effectiveBits) {
        super(effectiveBits);
    }

    @Override
    protected boolean supportsMultipleChannelsPerSourceBuffer() {
        return false;
    }

    @Override
    protected int bytesPerVoxel() {
        return 2;
    }

    @Override
    protected void copyKeepOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedByteBuffer destination) {

        byte[] sourceArray = source.array();

        for (int index = 0; index < sourceSize; index += sourceIncrement) {

            int value = extractScaledValue(sourceArray, index, channelIndexRelative, littleEndian);

            destination.putUnsigned(value);
        }
    }

    @Override
    protected void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedByteBuffer destination,
            OrientationChange orientationCorrection) {

        byte[] sourceArray = source.array();

        int x = 0;
        int y = 0;

        for (int index = 0; index < sourceSize; index += sourceIncrement) {

            int value = extractScaledValue(sourceArray, index, channelIndexRelative, littleEndian);

            int indexOut = orientationCorrection.index(x, y, extent);
            destination.putUnsigned(indexOut, value);

            x++;
            if (x == extent.x()) {
                y++;
                x = 0;
            }
        }
    }

    /** Extracts a value from the source-array, and apply any scaling and clamping. */
    private int extractScaledValue(
            byte[] sourceArray, int index, int channelIndexRelative, boolean littleEndian) {
        int indexPlus = index + (channelIndexRelative * 2);

        int value = DataTools.bytesToShort(sourceArray, indexPlus, 2, littleEndian);

        // Make unsigned
        if (value < 0) {
            value += 65536;
        }

        return scaleAndClampValue(value);
    }

    private int scaleAndClampValue(int value) {
        value = scaleValue(value);

        if (value > 255) {
            value = 255;
        }
        if (value < 0) {
            value = 0;
        }
        return value;
    }
}
