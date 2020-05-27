package org.anchoranalysis.image.binary.voxel;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class BinaryVoxelBoxByte extends BinaryVoxelBox<ByteBuffer> {
	
	private BinaryValuesByte bvb;
	
	public BinaryVoxelBoxByte(VoxelBox<ByteBuffer> voxelBox,
			BinaryValues bv) {
		super(voxelBox, new BinaryValues(bv) );
		this.bvb = getBinaryValues().createByte();
	}
	
	@Override
	public boolean isHigh(int x, int y, int z) {
		int offset = getVoxelBox().extent().offset(x, y);
		return getVoxelBox().getPixelsForPlane(z).buffer().get(offset) != bvb.getOffByte();
	}

	@Override
	public boolean isLow(int x, int y, int z) {
		return !isHigh(x, y, z);
	}
	
	@Override
	public void setHigh(int x, int y, int z) {
		int offset = getVoxelBox().extent().offset(x, y);
		getVoxelBox().getPixelsForPlane(z).buffer().put(offset, bvb.getOnByte() );
	}

	@Override
	public void setLow(int x, int y, int z) {
		int offset = getVoxelBox().extent().offset(x, y);
		getVoxelBox().getPixelsForPlane(z).buffer().put(offset, bvb.getOffByte() );
	}

	public BinaryValuesByte getBinaryValuesByte() {
		return bvb;
	}
	
	@Override
	public BinaryVoxelBoxByte duplicate() {
		return new BinaryVoxelBoxByte( getVoxelBox().duplicate(), getBinaryValues().duplicate() );
	}
	
	public BinaryVoxelBox<ByteBuffer> extractSlice(int z) throws CreateException {
		return new BinaryVoxelBoxByte( getVoxelBox().extractSlice(z), getBinaryValues().duplicate() );
	}
}
