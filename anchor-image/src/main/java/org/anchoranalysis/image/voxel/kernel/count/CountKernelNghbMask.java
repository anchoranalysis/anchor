package org.anchoranalysis.image.voxel.kernel.count;

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
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

/**
 * The number of touching-faces of a voxel with a neighbour, so long as the neighbour is part of a mask
 * 
 * i.e. the sum of all faces of a voxel that touch the face of a voxel belonging to a neighbouring pixel
 * 
 * @author Owen Feehan
 *
 */
public class CountKernelNghbMask extends CountKernelNghbBase {
	
	private BinaryVoxelBox<ByteBuffer> vbRequireHigh;
	private BinaryValuesByte bvRequireHigh;
	private ObjectMask omRequireHigh;

	private LocalSlices localSlicesRequireHigh;
	
	public CountKernelNghbMask(boolean useZ, BinaryValuesByte bv, ObjectMask omRequireHigh, boolean multipleMatchesPerVoxel) {
		super(useZ, bv, multipleMatchesPerVoxel);
		this.omRequireHigh = omRequireHigh;
		this.vbRequireHigh = omRequireHigh.binaryVoxelBox();
		this.bvRequireHigh = vbRequireHigh.getBinaryValues().createByte();
	}

	@Override
	public void notifyZChange(LocalSlices inSlices, int z) {
		super.notifyZChange(inSlices, z);
		localSlicesRequireHigh = new LocalSlices(z + omRequireHigh.getBoundingBox().cornerMin().getZ(),3, vbRequireHigh.getVoxelBox());
	}
	
	@Override
	protected boolean isNghbVoxelAccepted( Point3i pnt, int xShift, int yShift, int zShift, Extent extent ) {
		
		ByteBuffer inArr = localSlicesRequireHigh.getLocal(zShift);
		
		if (inArr==null) {
			return false;
		}
		
		int x1 = pnt.getX() + omRequireHigh.getBoundingBox().cornerMin().getX() + xShift;
		
		if (!vbRequireHigh.extent().containsX(x1)) {
			return false;
		}
		
		int y1 = pnt.getY() + omRequireHigh.getBoundingBox().cornerMin().getY() + yShift; 

		if (!vbRequireHigh.extent().containsY(y1)) {
			return false;
		}
		
		int indexGlobal = vbRequireHigh.extent().offset(
			x1,
			y1
		);
		return bvRequireHigh.isOn(inArr.get(indexGlobal));
	}




	


}
