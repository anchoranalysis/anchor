package org.anchoranalysis.image.voxel.kernel;

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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public abstract class Kernel {

	private int size;
	private int sizeHalf;

	// Only use odd sizes
	public Kernel(int size) {
		super();
		this.size = size;
		this.sizeHalf = (size-1) / 2;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSizeHalf() {
		return sizeHalf;
	}
	
	public int getYMin( Point3i pnt ) {
		return Math.max( pnt.getY() - getSizeHalf(), 0 );
	}
	
	public int getYMax( Point3i pnt, Extent extnt ) {
		return Math.min( pnt.getY() + getSizeHalf(), extnt.getY() - 1 );
	}

	public int getXMin( Point3i pnt ) {
		return Math.max( pnt.getX() - getSizeHalf(), 0 );
	}
	
	public int getXMax( Point3i pnt, Extent extnt ) {
		return Math.min( pnt.getX() + getSizeHalf(), extnt.getX() - 1 );
	}
	
	public abstract void init( VoxelBox<ByteBuffer> in );
	
	public abstract void notifyZChange( LocalSlices inSlices, int z );
}
