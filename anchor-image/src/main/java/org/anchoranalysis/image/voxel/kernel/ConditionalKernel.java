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
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.box.VoxelBox;

// Erosion with a 3x3 or 3x3x3 kernel
public class ConditionalKernel extends BinaryKernel {
	

	private BinaryKernel kernel;
	private int minValue;
	private VoxelBox<ByteBuffer> vbIntensity;
	
	// Constructor
	public ConditionalKernel( BinaryKernel kernel, int minValue, VoxelBox<ByteBuffer> vbIntensity ) {
		super(kernel.getSize());
		this.kernel = kernel;
		this.minValue = minValue;
		this.vbIntensity = vbIntensity;
	}

	@Override
	public boolean accptPos( int ind, Point3i pnt ) {

		byte valByte = vbIntensity.getPixelsForPlane(pnt.getZ()).buffer().get(
			vbIntensity.extent().offsetSlice(pnt)
		);
		int val = ByteConverter.unsignedByteToInt(valByte);
		
		if (val<minValue) {
			return false;
		}
		
		return kernel.accptPos(ind, pnt);
	}

	@Override
	public void init(VoxelBox<ByteBuffer> in) {
		kernel.init(in);
	}

	@Override
	public void notifyZChange(LocalSlices inSlices, int z) {
		kernel.notifyZChange(inSlices, z);
	}


}
