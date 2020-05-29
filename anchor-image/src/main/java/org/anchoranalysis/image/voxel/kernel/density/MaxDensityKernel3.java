package org.anchoranalysis.image.voxel.kernel.density;

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
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

// Erosion with a 3x3 or 3x3x3 kernel
public class MaxDensityKernel3 extends BinaryKernel {
	
	private boolean outsideAtThreshold = false;
	
	private boolean useZ;
	
	private BinaryValuesByte bv;
	private int maxCnt;
	
	private LocalSlices inSlices;
	
	private Extent extent;
	
	// Constructor
	public MaxDensityKernel3(BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ, int maxCnt) {
		super(3);
		this.outsideAtThreshold = outsideAtThreshold;
		this.useZ = useZ;
		this.bv = bv;
		this.maxCnt = maxCnt;
	}
	
	@Override
	public void init(VoxelBox<ByteBuffer> in) {
		this.extent = in.extent();
	}

	@Override
	public void notifyZChange(LocalSlices inSlices, int z) {
		this.inSlices = inSlices;
	}

	@Override
	public boolean accptPos( int ind, Point3i pnt ) {

		int cnt = 0;
		
		ByteBuffer inArr_Z = inSlices.getLocal(0);
		ByteBuffer inArr_ZLess1 = inSlices.getLocal(-1);
		ByteBuffer inArr_ZPlus1 = inSlices.getLocal(+1);
		
		int xLength = extent.getX();
		
		int x = pnt.getX();
		int y = pnt.getY();
		
		if (bv.isOn(inArr_Z.get(ind))) {
			cnt++;
		}
		
		// We walk up and down in x
		x--;
		ind--;
		if (x>=0) {
			if (bv.isOn(inArr_Z.get(ind))) {
				cnt++;
			}
		} else {
			if (outsideAtThreshold) {
				cnt++;
			}
		}
		
		x += 2;
		ind += 2;
		if (x<extent.getX()) {
			if (bv.isOn(inArr_Z.get(ind))) {
				cnt++;
			}
		} else {
			if (outsideAtThreshold) {
				cnt++;
			}
		}
		x--;
		ind--;
		
		
		// We walk up and down in y
		y--;
		ind -= xLength;
		if (y>=0) {
			if (bv.isOn(inArr_Z.get(ind))) {
				cnt++;
			}
		} else {
			if (outsideAtThreshold) {
				cnt++;
			}
		}
		
		y += 2;
		ind += (2*xLength);
		if (y<(extent.getY())) {
			if (bv.isOn(inArr_Z.get(ind))) {
				cnt++;
			}
		} else {
			if (outsideAtThreshold) {
				cnt++;
			}
		}
		y--;
		ind -= xLength;
		
		
		if (useZ) {
			
			if (inArr_ZLess1!=null) {
				if (bv.isOn(inArr_ZLess1.get(ind))) {
					cnt++;
				}
			} else {
				if (outsideAtThreshold) {
					cnt++;
				}
			}
			
			if (inArr_ZPlus1!=null) {
				if (bv.isOn(inArr_ZPlus1.get(ind))) {
					cnt++;
				}
			} else {
				if (outsideAtThreshold) {
					cnt++;;
				}
			}
		}
		
		return cnt <= maxCnt;
	}

}
