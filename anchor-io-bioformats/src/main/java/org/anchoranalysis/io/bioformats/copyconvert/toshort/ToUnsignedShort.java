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

package org.anchoranalysis.io.bioformats.copyconvert.toshort;

import java.nio.ByteBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferWrap;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

/**
 * Base class for implementations of {@link ConvertTo} that convert to <i>unsigned short</i>
 * buffers.
 *
 * @author Owen Feehan
 */
public abstract class ToUnsignedShort extends ConvertTo<UnsignedShortBuffer> {

    private static final int BYTES_PER_VOXEL = 2;

    /** Constructor populating super-class with necessary values. */
    protected ToUnsignedShort() {
        super(
                VoxelsUntyped::asShort,
                UnsignedShortBuffer::allocate,
                VoxelBufferWrap::unsignedShortBuffer);
    }

    @Override
    protected boolean supportsMultipleChannelsPerSourceBuffer() {
        return true;
    }

    /**
     * Converts a value retrieved from the source buffer to the destination form.
     *
     * @param value the value retrieved from the source buffer.
     * @return the converted value in the form to match the destination.
     */
    protected abstract short convertValue(short value);

    @Override
    protected void copyKeepOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedShortBuffer destination) {
        byte[] sourceArray = source.array();
        for (int index = channelIndexRelative; index < sourceSize; index += sourceIncrement) {
            short value = extractConvertedValue(sourceArray, index, littleEndian);
            destination.putRaw(value);
        }
    }

    @Override
    protected void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedShortBuffer destination,
            OrientationChange orientationCorrection) {

        byte[] sourceArray = source.array();
        int x = 0;
        int y = 0;

        for (int index = channelIndexRelative; index < sourceSize; index += sourceIncrement) {
            short value = extractConvertedValue(sourceArray, index, littleEndian);

            int indexOut = orientationCorrection.index(x, y, extent);

            destination.putRaw(indexOut, value);

            x++;
            if (x == extent.x()) {
                y++;
                x = 0;
            }
        }
    }

    private short extractConvertedValue(byte[] sourceArray, int index, boolean littleEndian) {
        short value = valueFromBuffer(sourceArray, index, littleEndian);
        return convertValue(value);
    }

    private short valueFromBuffer(byte[] buffer, int index, boolean littleEndian) {
        return DataTools.bytesToShort(buffer, index, BYTES_PER_VOXEL, littleEndian);
    }

    @Override
    protected int bytesPerVoxel() {
        return BYTES_PER_VOXEL;
    }
}
