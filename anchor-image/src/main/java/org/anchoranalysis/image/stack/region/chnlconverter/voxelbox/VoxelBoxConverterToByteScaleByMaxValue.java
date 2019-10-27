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
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

// Converts voxel buffers to a unsigned 8-bit buffer scaling against the maximum constant value.
// Linear between this values
//
// Note that the Type.MAX_VALUE in Java assumes siged types.  So we multiply by two to get unsigned sizes
public class VoxelBoxConverterToByteScaleByMaxValue extends VoxelBoxConverter<ByteBuffer> {

	private double scale = 0;

	public VoxelBoxConverterToByteScaleByMaxValue(int maxValue) {
		super();
		setMaxValue(maxValue);
	}
	
	public void setMaxValue( int maxValue ) {
		this.scale = 255.0/maxValue;
	}


	// This doesn't really make sense for a float, as the maximum value is so much higher, so we take
	//  it as being the same as Integer.MAX_VALUE
	@Override
	public VoxelBuffer<ByteBuffer> convertFromFloat(VoxelBuffer<FloatBuffer> bufferIn) {
		
		ByteBuffer bufferOut = ByteBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			float val = bufferIn.buffer().get();
			
			val = (int) scale*val;
			
			if (val>255) {
				val= 255;
			}
			if (val<0) {
				val = 0;
			}
						
			bufferOut.put( (byte) val );
		}
		
		return VoxelBufferByte.wrap(bufferOut);
	}

	@Override
	public VoxelBuffer<ByteBuffer> convertFromInt(VoxelBuffer<IntBuffer> bufferIn) {
		
		ByteBuffer bufferOut = ByteBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			
			long valOrig = bufferIn.buffer().get();
			
			double val = scale*valOrig;
			
			if (val>255) {
				val= 255;
			}
			if (val<0) {
				val = 0;
			}
						
			bufferOut.put( (byte) val );
		}
		
		return VoxelBufferByte.wrap(bufferOut);
	}

	@Override
	public VoxelBuffer<ByteBuffer> convertFromShort(VoxelBuffer<ShortBuffer> bufferIn) {
		
		ByteBuffer bufferOut = ByteBuffer.allocate( bufferIn.buffer().capacity() );
		
		while( bufferIn.buffer().hasRemaining() ) {
			
			int valOrig = ByteConverter.unsignedShortToInt( bufferIn.buffer().get() );
			
			double val = scale*valOrig;
			
			if (val>255) {
				val= 255;
			}
			if (val<0) {
				val = 0;
			}
						
			bufferOut.put( (byte) val );
		}
		
		return VoxelBufferByte.wrap(bufferOut);
	}

	@Override
	public VoxelBuffer<ByteBuffer> convertFromByte(VoxelBuffer<ByteBuffer> in) {
		return in.duplicate();
	}
}
