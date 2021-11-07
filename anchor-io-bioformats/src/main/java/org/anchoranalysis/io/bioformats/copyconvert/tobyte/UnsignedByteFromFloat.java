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

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import loci.common.DataTools;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Converts a {@link ByteBuffer} encoding a <i>float</i> type to <i>unsigned byte</i> type, as
 * expected in an Anchor {@link VoxelBuffer}.
 *
 * <p>Only values in the range {@code 0 <= value <= 255} are preserved. Any values outside this
 * range are clamped to {@code 0} or {@code 255}.
 *
 * <p>Any decimal component is dropped.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class UnsignedByteFromFloat extends ToUnsignedByte {

    private Extent extent;

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        super.setupBefore(dimensions, numberChannelsPerArray);
        this.extent = dimensions.extent();
    }

    @Override
    protected UnsignedByteBuffer convert(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection) {
        Preconditions.checkArgument(channelIndexRelative == 0, "interleaving not supported");

        UnsignedByteBuffer destination = allocateBuffer();

        byte[] sourceArray = source.array();
        boolean littleEndian = source.order() == ByteOrder.LITTLE_ENDIAN;

        if (orientationCorrection == OrientationChange.KEEP_UNCHANGED) {
            copyKeepOrientation(sourceArray, littleEndian, destination);
        } else {
            copyChangeOrientation(sourceArray, littleEndian, destination, orientationCorrection);
        }

        return destination;
    }

    @Override
    protected int calculateBytesPerPixel(int numberChannelsPerArray) {
        return 4;
    }

    /**
     * Copy the bytes, without changing orientation.
     *
     * <p>This is kept separate to {@link #copyChangeOrientation(byte[], boolean,
     * UnsignedByteBuffer, OrientationChange)} as it can be done slightly more efficiently.
     */
    private void copyKeepOrientation(
            byte[] sourceArray, boolean littleEndian, UnsignedByteBuffer destination) {
        for (int index = 0; index < sizeBytes; index += bytesPerPixel) {
            float value = extractClampedValue(sourceArray, index, littleEndian);
            destination.putFloat(value);
        }
    }

    /** Copy the bytes, changing orientation. */
    private void copyChangeOrientation(
            byte[] sourceArray,
            boolean littleEndian,
            UnsignedByteBuffer destination,
            OrientationChange orientationCorrection) {
        int x = 0;
        int y = 0;

        for (int index = 0; index < sizeBytes; index += bytesPerPixel) {
            float value = extractClampedValue(sourceArray, index, littleEndian);

            int indexOut = orientationCorrection.index(x, y, extent);
            destination.putFloat(indexOut, value);

            x++;
            if (x == extent.x()) {
                y++;
                x = 0;
            }
        }
    }

    /** Extracts a value from the source-array, and apply any clamping. */
    private float extractClampedValue(byte[] sourceArray, int index, boolean littleEndian) {
        float value = DataTools.bytesToFloat(sourceArray, index, littleEndian);

        if (value > 255) {
            value = 255;
        }
        if (value < 0) {
            value = 0;
        }
        return value;
    }
}
