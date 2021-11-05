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
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Converts a {@link ByteBuffer} encoding <i>unsigned byte</i>s (<i>with interleaving</i>) to
 * <i>unsigned byte</i> type, as expected in an Anchor {@link VoxelBuffer}.
 *
 * @author Owen Feehan
 */
public class UnsignedByteFromUnsignedByteInterleaving extends ToUnsignedByte {

    private int numberChannelsPerArray;
    private Extent extent;

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        super.setupBefore(dimensions, numberChannelsPerArray);
        this.numberChannelsPerArray = numberChannelsPerArray;
        this.extent = dimensions.extent();
    }

    @Override
    protected UnsignedByteBuffer convert(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection) {

        if (source.capacity() == sizeXY
                && channelIndexRelative == 0
                && orientationCorrection == OrientationChange.KEEP_UNCHANGED) {
            // Reuse the existing buffer, if it's single channeled, and has no orientation change.
            return UnsignedByteBuffer.wrapRaw(source);
        } else {
            UnsignedByteBuffer destination = allocateBuffer();

            // Loop through the relevant positions
            int totalBytesSource = sizeXY * numberChannelsPerArray;

            if (orientationCorrection == OrientationChange.KEEP_UNCHANGED) {
                copyKeepOrientation(source, channelIndexRelative, destination, totalBytesSource);
            } else {
                copyChangeOrientation(
                        source,
                        channelIndexRelative,
                        destination,
                        totalBytesSource,
                        orientationCorrection);
            }

            return destination;
        }
    }

    @Override
    protected int calculateBytesPerPixel(int numberChannelsPerArray) {
        return 1;
    }

    /**
     * Copy the bytes, without changing orientation.
     *
     * <p>This is kept separate to {@link #copyChangeOrientation(ByteBuffer, int,
     * UnsignedByteBuffer, int, OrientationChange)} as it can be done slightly more efficiently.
     */
    private void copyKeepOrientation(
            ByteBuffer source,
            int channelIndexRelative,
            UnsignedByteBuffer destination,
            int totalBytesSource) {
        for (int indexIn = channelIndexRelative;
                indexIn < totalBytesSource;
                indexIn += numberChannelsPerArray) {
            byte value = source.get(indexIn);

            destination.putRaw(value);
        }
    }

    /** Copy the bytes, changing orientation. */
    private void copyChangeOrientation(
            ByteBuffer source,
            int channelIndexRelative,
            UnsignedByteBuffer destination,
            int totalBytesSource,
            OrientationChange orientationCorrection) {
        int x = 0;
        int y = 0;

        for (int indexIn = channelIndexRelative;
                indexIn < totalBytesSource;
                indexIn += numberChannelsPerArray) {
            byte value = source.get(indexIn);

            int indexChanged = orientationCorrection.index(x, y, extent);
            destination.putRaw(indexChanged, value);

            x++;
            if (x == extent.x()) {
                y++;
                x = 0;
            }
        }
    }
}
