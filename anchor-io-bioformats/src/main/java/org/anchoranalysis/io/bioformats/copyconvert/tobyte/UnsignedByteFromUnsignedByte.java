/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Like {@link ToUnsignedByte} and provides common functionality when converting to an <i>unsigned
 * byte</i>.
 *
 * <p>As this is the same as the source type, optimizations can occur to copy the existing memory
 * without further manipulation.
 *
 * @author Owen Feehan
 */
public abstract class UnsignedByteFromUnsignedByte extends ToUnsignedByte {

    @Override
    protected boolean supportsMultipleChannelsPerSourceBuffer() {
        return true;
    }

    @Override
    protected int bytesPerVoxel() {
        return UnsignedByteVoxelType.INSTANCE.numberBytes();
    }

    @Override
    protected UnsignedByteBuffer convert(
            ByteBuffer source,
            int channelIndexRelative,
            OrientationChange orientationCorrection,
            boolean littleEndian)
            throws IOException {
        if (isSourceIdenticalToDestination(source, channelIndexRelative, orientationCorrection)) {
            // Reuse the existing buffer, if it's single channeled
            return UnsignedByteBuffer.wrapRaw(source);
        } else {
            return super.convert(source, channelIndexRelative, orientationCorrection, littleEndian);
        }
    }

    private boolean isSourceIdenticalToDestination(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection) {
        return source.capacity() == destinationSize
                && sourceIncrement == destinationSize
                && channelIndexRelative == 0
                && orientationCorrection == OrientationChange.KEEP_UNCHANGED;
    }
}
