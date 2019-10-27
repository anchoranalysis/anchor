package org.anchoranalysis.image.stack.region.chnlconverter.voxelbox;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;

// Converts voxel buffers to a unsigned 8-bit buffer without scaling any values.
// So values larger than 255 are clipped
public class VoxelBoxConverterToFloatNoScaling extends VoxelBoxConverter<FloatBuffer> {

	@Override
	public VoxelBuffer<FloatBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {
		return bufferIn.duplicate();
	}

	@Override
	public VoxelBuffer<FloatBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {

		FloatBuffer bufferOut = FloatBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			bufferOut.put( (float) bufferIn.buffer().get() );
		}
		
		return VoxelBufferFloat.wrap(bufferOut);
	}

	@Override
	public VoxelBuffer<FloatBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {

		FloatBuffer bufferOut = FloatBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			bufferOut.put( (float) bufferIn.buffer().get() );
		}
		
		return VoxelBufferFloat.wrap(bufferOut);
	}

	@Override
	public VoxelBuffer<FloatBuffer> convertFromByte(VoxelBuffer<ByteBuffer> bufferIn) {

		VoxelBufferFloat bufferOut = VoxelBufferFloat.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			bufferOut.buffer().put( (float) ByteConverter.unsignedByteToInt(bufferIn.buffer().get()) );
		}
		
		return bufferOut;
	}
}
