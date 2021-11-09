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

import java.io.IOException;
import java.nio.ByteBuffer;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Converts a {@link ByteBuffer} encoding <i>unsigned byte</i>s (<i>no interleaving</i>) to
 * <i>unsigned byte</i> type, as expected in an Anchor {@link VoxelBuffer}.
 *
 * @author Owen Feehan
 */
public class UnsignedByteFromUnsignedByteNoInterleaving extends ToUnsignedByte {

    @Override
    protected boolean supportsInterleaving() {
        return false;
    }

    @Override
    protected UnsignedByteBuffer convert(
            ByteBuffer source,
            int channelIndexRelative,
            OrientationChange orientationCorrection,
            boolean littleEndian)
            throws IOException {
        if (source.capacity() == sizeXY
                && channelIndexRelative == 0
                && orientationCorrection == OrientationChange.KEEP_UNCHANGED) {
            // Reuse the existing buffer, if it's single channeled
            return UnsignedByteBuffer.wrapRaw(source);
        } else {
            return super.convert(source, channelIndexRelative, orientationCorrection, littleEndian);
        }
    }

    @Override
    protected int bytesPerVoxel(int numberChannelsPerArray) {
        return 1;
    }

    @Override
    protected void copyKeepOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedByteBuffer destination) {
        source.position(sizeBytes * channelIndexRelative);
        source.limit(source.position() + sizeBytes);
        destination.put(source);
    }

    @Override
    protected void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedByteBuffer destination,
            OrientationChange orientationCorrection) {
        int sourceOffset = sizeBytes * channelIndexRelative;
        for (int y = 0; y < extent.y(); y++) {
            for (int x = 0; x < extent.x(); x++) {

                byte value = source.get(sourceOffset);

                int destinationOffset = orientationCorrection.index(sourceOffset, x, y, extent);
                destination.putRaw(destinationOffset, value);
                sourceOffset++;
            }
        }
    }
}
