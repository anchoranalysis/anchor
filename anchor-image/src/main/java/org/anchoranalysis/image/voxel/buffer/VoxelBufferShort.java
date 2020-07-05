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


import java.nio.ShortBuffer;

import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public final class VoxelBufferShort extends VoxelBuffer<ShortBuffer> {

	private final ShortBuffer delegate;
	
	public VoxelBufferShort(ShortBuffer delegate) {
		super();
		this.delegate = delegate;
	}
	
	public static VoxelBufferShort allocate( int size ) {
		return new VoxelBufferShort( ShortBuffer.allocate(size) );
	}
	
	public static VoxelBufferShort wrap( short[] arr ) {
		return new VoxelBufferShort( ShortBuffer.wrap(arr) );
	}
	
	public static VoxelBufferShort wrap( ShortBuffer buffer ) {
		return new VoxelBufferShort( buffer );
	}
	
	@Override
	public ShortBuffer buffer() {
		return delegate;
	}
	
	@Override
	public VoxelBuffer<ShortBuffer> duplicate() {
		return new VoxelBufferShort(ByteConverter.copy(delegate));
	}
	
	@Override
	public VoxelDataType dataType() {
		return VoxelDataTypeUnsignedShort.INSTANCE;
	}
	
	@Override
	public int getInt(int index) {
		return ByteConverter.unsignedShortToInt( delegate.get(index) );
	}
	
	@Override
	public void putInt(int index, int val) {
		delegate.put(index, (short) val);
	}

	@Override
	public void putByte(int index, byte val) {
		delegate.put(index, (short) ByteConverter.unsignedByteToInt(val) );
	}
	
	@Override
	public void transferFrom(int destIndex, VoxelBuffer<ShortBuffer> src, int srcIndex) {
		delegate.put( destIndex, src.buffer().get(srcIndex) );	
	}

	@Override
	public int size() {
		return delegate.capacity();
	}

}
