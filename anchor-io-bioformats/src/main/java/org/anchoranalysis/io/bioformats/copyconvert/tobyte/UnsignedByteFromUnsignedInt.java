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
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Converts data of type <i>unsigned int</i> to <i>unsigned byte</i>.
 *
 * <p>If more than 8-bits are being used in the input values, scaling is applied to map the range of
 * effective-bits (how many bits are used) to an 8-bit range.
 *
 * @author Owen Feehan
 */
public class UnsignedByteFromUnsignedInt extends ToUnsignedByteWithScaling {

    private Extent extent;

    /**
     * Create with a number of effective-bits.
     *
     * @param effectiveBits the number of bits that are used in the input-type e.g. 8 or 12 or 16.
     */
    public UnsignedByteFromUnsignedInt(int effectiveBits) {
        super(effectiveBits);
    }

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        super.setupBefore(dimensions, numberChannelsPerArray);
        this.extent = dimensions.extent();
    }

    @Override
    protected boolean supportsInterleaving() {
        return false;
    }

    @Override
    protected int bytesPerVoxel(int numberChannelsPerArray) {
        return 4;
    }

    @Override
    protected void copyKeepOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedByteBuffer destination) {
        byte[] sourceArray = source.array();
        for (int index = 0; index < sizeBytes; index += bytesPerPixel) {
            int value = extractScaledValue(sourceArray, index, littleEndian);
            destination.putDouble(value);
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

        for (int index = 0; index < sizeBytes; index += bytesPerPixel) {
            int value = extractScaledValue(sourceArray, index, littleEndian);

            int indexOut = orientationCorrection.index(x, y, extent);
            destination.putDouble(indexOut, value);

            x++;
            if (x == extent.x()) {
                y++;
                x = 0;
            }
        }
    }

    /** Extracts a value from the source-array, and apply any scaling and clamping. */
    private int extractScaledValue(byte[] sourceArray, int index, boolean littleEndian) {
        int value = DataTools.bytesToInt(sourceArray, index, littleEndian);
        return scaleValue(value);
    }
}
