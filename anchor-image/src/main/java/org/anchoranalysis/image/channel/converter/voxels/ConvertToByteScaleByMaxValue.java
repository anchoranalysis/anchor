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

import java.nio.FloatBuffer;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferUnsignedByte;

// Converts voxel buffers to a unsigned 8-bit buffer scaling against the maximum constant value.
// Linear between this values
//
// Note that the Type.MAX_VALUE in Java assumes siged types.  So we multiply by two to get unsigned
// sizes
public final class ConvertToByteScaleByMaxValue extends VoxelsConverter<UnsignedByteBuffer> {

    private double scale = 0;

    public ConvertToByteScaleByMaxValue(int maxValue) {
        super();
        setMaxValue(maxValue);
    }

    public void setMaxValue(long maxValue) {
        this.scale = 255.0 / maxValue;
    }

    // This doesn't really make sense for a float, as the maximum value is so much higher, so we
    // take
    //  it as being the same as Integer.MAX_VALUE
    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {
            float value = bufferIn.buffer().get();

            value = (int) scale * value;

            if (value > 255) {
                value = 255;
            }
            if (value < 0) {
                value = 0;
            }

            bufferOut.putFloat(value);
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromInt(VoxelBuffer<UnsignedIntBuffer> bufferIn) {

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {

            double value = scale * bufferIn.buffer().getUnsigned();

            if (value > 255) {
                value = 255;
            }
            if (value < 0) {
                value = 0;
            }

            bufferOut.putDouble(value);
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromShort(
            VoxelBuffer<UnsignedShortBuffer> bufferIn) {

        UnsignedByteBuffer bufferOut = UnsignedByteBuffer.allocate(bufferIn.buffer().capacity());

        while (bufferIn.buffer().hasRemaining()) {

            double value = scale * bufferIn.buffer().getUnsigned();

            if (value > 255) {
                value = 255;
            }
            if (value < 0) {
                value = 0;
            }

            bufferOut.putDouble(value);
        }

        return VoxelBufferUnsignedByte.wrapBuffer(bufferOut);
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> convertFromByte(VoxelBuffer<UnsignedByteBuffer> in) {
        return in.duplicate();
    }
}
