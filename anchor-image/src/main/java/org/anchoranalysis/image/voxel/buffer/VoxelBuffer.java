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


import java.nio.Buffer;

import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public abstract class VoxelBuffer<BufferType extends Buffer> {

	public abstract VoxelDataType dataType();
	
	public abstract BufferType buffer();
	
	public abstract VoxelBuffer<BufferType> duplicate();
	
	// Gets the underlying buffer-item converted to an int
	public abstract int getInt( int index );

	public abstract void putInt( int index, int val );
	
	public abstract void putByte( int index, byte val );
	
	public abstract int size();
	
	public void transferFrom( int destIndex, VoxelBuffer<BufferType> src ) {
		transferFrom( destIndex, src, destIndex );
	}

	public void transferFromConvert(int destIndex, VoxelBuffer<?> src,
			int srcIndex) {
		int val = src.getInt(srcIndex);
		putInt(destIndex, val);
	}
	
	@Override
	public String toString() {
		Histogram h = HistogramFactory.create(this);
		return h.toString();
	}
	
	public abstract void transferFrom( int destIndex, VoxelBuffer<BufferType> src, int srcIndex );
}
