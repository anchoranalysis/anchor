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

package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public final class ToByteNoScaling implements VoxelsConverter<ByteBuffer> {

    @Override
    public VoxelBuffer<ByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            float f = bufferIn.buffer().get();
            if (f > VoxelDataTypeUnsignedByte.MAX_VALUE_INT) {
                f = VoxelDataTypeUnsignedByte.MAX_VALUE_INT;
            }
            if (f < 0) {
                f = 0;
            }
            bufferOut.put((byte) f);
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) ByteConverter.unsignedIntToLong(bufferIn.buffer().get()));
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {

        ByteBuffer bufferOut = ByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            bufferOut.put((byte) ByteConverter.unsignedShortToInt(bufferIn.buffer().get()));
        }

        return VoxelBufferByte.wrap(bufferOut);
    }

    @Override
    public VoxelBuffer<ByteBuffer> convertFromByte(VoxelBuffer<ByteBuffer> in) {
        return in.duplicate();
    }
}
