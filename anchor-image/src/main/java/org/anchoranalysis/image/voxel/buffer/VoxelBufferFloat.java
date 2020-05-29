package org.anchoranalysis.image.voxel.buffer;

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


import java.nio.FloatBuffer;

import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;

public final class VoxelBufferFloat extends VoxelBuffer<FloatBuffer> {

	private final FloatBuffer delegate;

	private VoxelBufferFloat(FloatBuffer delegate) {
		super();
		this.delegate = delegate;
	}
	
	public static VoxelBufferFloat wrap( float[] arr ) {
		return new VoxelBufferFloat( FloatBuffer.wrap(arr) );
	}
	
	public static VoxelBufferFloat wrap( FloatBuffer buffer ) {
		return new VoxelBufferFloat( buffer );
	}

	@Override
	public FloatBuffer buffer() {
		return delegate;
	}
	
	@Override
	public VoxelBuffer<FloatBuffer> duplicate() {
		return new VoxelBufferFloat(ByteConverter.copy(delegate));
	}
	
	@Override
	public VoxelDataType dataType() {
		return VoxelDataTypeFloat.instance;
	}
	
	public static VoxelBufferFloat allocate( int size ) {
		return new VoxelBufferFloat( FloatBuffer.allocate(size) );
	}

	@Override
	public int getInt(int index) {
		return (int) delegate.get(index);
	}
	
	@Override
	public void putInt(int index, int val) {
		delegate.put(index, (float) val);
	}

	@Override
	public void putByte(int index, byte val) {
		delegate.put(index, (float) ByteConverter.unsignedByteToInt(val) );
	}
	
	@Override
	public void transferFrom(int destIndex, VoxelBuffer<FloatBuffer> src, int srcIndex) {
		delegate.put( destIndex, src.buffer().get(srcIndex) );
	}
	
	@Override
	public int size() {
		return delegate.capacity();
	}

}
