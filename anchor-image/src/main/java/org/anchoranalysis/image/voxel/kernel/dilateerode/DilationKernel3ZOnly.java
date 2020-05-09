package org.anchoranalysis.image.voxel.kernel.dilateerode;

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
import org.anchoranalysis.image.voxel.box.VoxelBox;

// Erosion with a 3x3 or 3x3x3 kernel
public final class DilationKernel3ZOnly extends BinaryKernelMorph3 {
	
	
	
	// Constructor
	public DilationKernel3ZOnly(BinaryValuesByte bv, boolean outsideAtThreshold) {
		super(bv, outsideAtThreshold);
	}
	
	@Override
	public void init(VoxelBox<ByteBuffer> in) {
	}

	@Override
	public boolean accptPos( int ind, Point3i pnt ) {

		ByteBuffer inArr_Z = inSlices.getLocal(0);
		ByteBuffer inArr_ZLess1 = inSlices.getLocal(-1);
		ByteBuffer inArr_ZPlus1 = inSlices.getLocal(+1);
		
		if (bv.isOn(inArr_Z.get(ind))) {
			return true;
		}
			
		if (inArr_ZLess1!=null) {
			if (bv.isOn(inArr_ZLess1.get(ind))) {
				return true;
			}
		} else {
			if (outsideAtThreshold) {
				return true;
			}
		}
		
		if (inArr_ZPlus1!=null) {
			if (bv.isOn(inArr_ZPlus1.get(ind))) {
				return true;
			}
		} else {
			if (outsideAtThreshold) {
				return true;
			}
		}

		
		return false;
	}

}
