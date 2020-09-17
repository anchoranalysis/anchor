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

package org.anchoranalysis.image.voxel.convert;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferUnsignedByte;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsRemaining;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinaryWithoutOffset;

// Converts voxel buffers to a unsigned 8-bit buffer scaling against a the minimum and maximum
// constant.
// Linear between these two limits.
//
// Note that the Type.MAX_VALUE in Java assumes siged types.  So we multiply by two to get unsigned
// sizes
public final class ConvertToByteScaleByMinMaxValue extends VoxelsConverter<UnsignedByteBuffer> {

    private float scale = 0;
    private int subtract = 0;

    public ConvertToByteScaleByMinMaxValue(int minValue, int maxValue) {
        super();
        setMinMaxValues(minValue, maxValue);
    }

    public void setMinMaxValues(int minValue, int maxValue) {
        this.scale = 255.0f / (maxValue - minValue);
        this.subtract = minValue;
    }

    // This doesn't really make sense for a float, as the maximum value is so much higher, so we
    // take
    //  it as being the same as Integer.MAX_VALUE
    @Override
    public void convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn, VoxelBuffer<UnsignedByteBuffer> bufferOut) {
        convertFrom(bufferIn, (in,out) ->
            out.putFloatClipped( scale * (in.get() - subtract) ));
    }

    @Override
    public void convertFromInt(VoxelBuffer<UnsignedIntBuffer> bufferIn, VoxelBuffer<UnsignedByteBuffer> bufferOut) {
        convertFrom(bufferIn, (in,out) ->
            out.putFloatClipped( scale * (in.getUnsigned() - subtract) ) );
    }

    @Override
    public void convertFromShort(
            VoxelBuffer<UnsignedShortBuffer> bufferIn, VoxelBuffer<UnsignedByteBuffer> bufferOut) {
        convertFrom(bufferIn, (in,out) ->
            out.putFloatClipped( scale * (in.getUnsigned() - subtract) ) );
    }

    @Override
    public void convertFromByte(VoxelBuffer<UnsignedByteBuffer> bufferIn, VoxelBuffer<UnsignedByteBuffer> bufferOut) {
        IterateVoxelsRemaining.withTwoBuffersWithoutOffset(bufferIn, bufferOut, (in,out) ->
            out.putRaw( in.getRaw() )
        );
    }
    
    private <S> VoxelBuffer<UnsignedByteBuffer> convertFrom( VoxelBuffer<S> bufferIn, ProcessBufferBinaryWithoutOffset<S,UnsignedByteBuffer> process ) {
        VoxelBuffer<UnsignedByteBuffer> bufferOut = VoxelBufferUnsignedByte.allocate(bufferIn.capacity());
        IterateVoxelsRemaining.withTwoBuffersWithoutOffset(bufferIn, bufferOut, process);
        return bufferOut;
    }
}
