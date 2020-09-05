/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.channel.converter.voxels;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.PrimitiveConverter;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public final class ConvertToShortNoScaling extends VoxelsConverter<ShortBuffer> {

    @Override
    public VoxelBuffer<ShortBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        ShortBuffer bufferOut = ShortBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            float f = bufferIn.buffer().get();
            if (f > UnsignedShortVoxelType.MAX_VALUE_INT) {
                f = UnsignedShortVoxelType.MAX_VALUE_INT;
            }
            if (f < 0) {
                f = 0;
            }
            bufferOut.put((short) f);
        }

        return VoxelBufferShort.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<ShortBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        ShortBuffer bufferOut = ShortBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) PrimitiveConverter.unsignedIntToShort(bufferIn.buffer().get()));
        }

        return VoxelBufferShort.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<ShortBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {
        return bufferIn.duplicate();
    }

    @Override
    public VoxelBuffer<ShortBuffer> convertFromByte(VoxelBuffer<UnsignedByteBuffer> bufferIn) {
        ShortBuffer bufferOut = ShortBuffer.allocate(bufferIn.size());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) bufferIn.buffer().getUnsignedByte());
        }

        return VoxelBufferShort.wrapBuffer(bufferOut);
    }
}
