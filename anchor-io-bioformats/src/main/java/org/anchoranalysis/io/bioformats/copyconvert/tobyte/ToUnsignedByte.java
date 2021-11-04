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
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferWrap;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.extracter.OrientationChange;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

/**
 * Converts a {@link ByteBuffer} encoding some data-type to a buffer of <i>unsigned byte</i> type,
 * as expected in an Anchor {@link VoxelBuffer}.
 *
 * @author Owen Feehan
 */
public abstract class ToUnsignedByte extends ConvertTo<UnsignedByteBuffer> {

    protected int sizeXY;
    protected int bytesPerPixel;
    protected int sizeBytes;

    protected ToUnsignedByte() {
        super(VoxelsUntyped::asByte);
    }

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        sizeXY = dimensions.volumeXY();
        bytesPerPixel = calculateBytesPerPixel(numberChannelsPerArray);
        sizeBytes = sizeXY * bytesPerPixel;
    }

    @Override
    protected VoxelBuffer<UnsignedByteBuffer> convertSliceOfSingleChannel(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection) {
        return VoxelBufferWrap.unsignedByteBuffer(
                convert(source, channelIndexRelative, orientationCorrection));
    }

    protected UnsignedByteBuffer allocateBuffer() {
        return UnsignedByteBuffer.allocate(sizeXY);
    }

    protected abstract UnsignedByteBuffer convert(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection);

    protected abstract int calculateBytesPerPixel(int numberChannelsPerArray);
}
