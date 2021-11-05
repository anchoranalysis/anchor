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
import java.nio.ByteOrder;
import loci.common.DataTools;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;
import org.anchoranalysis.spatial.box.Extent;

public abstract class ToUnsignedShort extends ConvertTo<UnsignedShortBuffer> {

    private static final int BYTES_PER_PIXEL = 2;

    private int sizeXY;
    private int sizeBytes;
    private int numberChannelsPerArray;
    private Extent extent;

    protected ToUnsignedShort() {
        super(VoxelsUntyped::asShort);
    }

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        this.sizeXY = dimensions.x() * dimensions.y();
        this.extent = dimensions.extent();
        this.sizeBytes = sizeXY * BYTES_PER_PIXEL * numberChannelsPerArray;
        this.numberChannelsPerArray = numberChannelsPerArray;
    }

    @Override
    protected VoxelBuffer<UnsignedShortBuffer> convertSliceOfSingleChannel(
            ByteBuffer sourceBuffer,
            int channelIndexRelative,
            OrientationChange orientationCorrection) {

        byte[] sourceArray = sourceBuffer.array();
        boolean littleEndian = sourceBuffer.order() == ByteOrder.LITTLE_ENDIAN;

        VoxelBuffer<UnsignedShortBuffer> voxels = VoxelBufferFactory.allocateUnsignedShort(sizeXY);

        int increment = numberChannelsPerArray * BYTES_PER_PIXEL;

        UnsignedShortBuffer destination = voxels.buffer();

        if (orientationCorrection == OrientationChange.KEEP_UNCHANGED) {
            copyKeepOrientation(
                    sourceArray, littleEndian, channelIndexRelative, increment, destination);
        } else {
            copyChangeOrientation(
                    sourceArray,
                    littleEndian,
                    channelIndexRelative,
                    increment,
                    destination,
                    orientationCorrection);
        }

        return voxels;
    }

    protected abstract short convertValue(short value);

    /**
     * Copy the bytes, without changing orientation.
     *
     * <p>This is kept separate to {@link #copyChangeOrientation(byte[], boolean, int, int,
     * UnsignedShortBuffer, OrientationChange)} as it can be done slightly more efficiently.
     */
    private void copyKeepOrientation(
            byte[] sourceArray,
            boolean littleEndian,
            int channelIndexRelative,
            int increment,
            UnsignedShortBuffer destination) {
        for (int index = channelIndexRelative; index < sizeBytes; index += increment) {
            short value = extractConvertedValue(sourceArray, index, littleEndian);
            destination.putRaw(value);
        }
    }

    /** Copy the bytes, changing orientation. */
    private void copyChangeOrientation(
            byte[] sourceArray,
            boolean littleEndian,
            int channelIndexRelative,
            int increment,
            UnsignedShortBuffer destination,
            OrientationChange orientationCorrection) {

        int x = 0;
        int y = 0;

        for (int index = channelIndexRelative; index < sizeBytes; index += increment) {
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
        return DataTools.bytesToShort(buffer, index, BYTES_PER_PIXEL, littleEndian);
    }
}
