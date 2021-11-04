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

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import loci.common.DataTools;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.extracter.OrientationChange;
import org.anchoranalysis.spatial.box.Extent;

public class UnsignedIntFromUnsignedInt extends ToInt {

    private static final int BYTES_PER_PIXEL = 4;

    private int sizeXY;
    private int sizeBytes;
    private Extent extent;

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        this.sizeXY = dimensions.x() * dimensions.y();
        this.sizeBytes = sizeXY * BYTES_PER_PIXEL;
        this.extent = dimensions.extent();
    }

    @Override
    protected VoxelBuffer<UnsignedIntBuffer> convertSliceOfSingleChannel(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection) {
        Preconditions.checkArgument(
                channelIndexRelative == 0, "interleaving not supported for int data");

        byte[] sourceArray = source.array();

        VoxelBuffer<UnsignedIntBuffer> voxels = VoxelBufferFactory.allocateUnsignedInt(sizeXY);
        UnsignedIntBuffer destination = voxels.buffer();

        boolean littleEndian = source.order() == ByteOrder.LITTLE_ENDIAN;

        if (orientationCorrection == OrientationChange.KEEP_UNCHANGED) {
            copyKeepOrientation(sourceArray, littleEndian, destination);
        } else {
            copyChangeOrientation(sourceArray, littleEndian, destination, orientationCorrection);
        }

        return voxels;
    }

    /**
     * Copy the bytes, without changing orientation.
     *
     * <p>This is kept separate to {@link #copyChangeOrientation(byte[], boolean, UnsignedIntBuffer,
     * OrientationChange)} as it can be done slightly more efficiently.
     */
    private void copyKeepOrientation(
            byte[] sourceArray, boolean littleEndian, UnsignedIntBuffer destination) {
        int indexOut = 0;
        for (int index = 0; index < sizeBytes; index += BYTES_PER_PIXEL) {
            int value = extractInt(sourceArray, index, littleEndian);
            destination.putRaw(indexOut++, value);
        }
    }

    /** Copy the bytes, changing orientation. */
    private void copyChangeOrientation(
            byte[] sourceArray,
            boolean littleEndian,
            UnsignedIntBuffer destination,
            OrientationChange orientationCorrection) {
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
