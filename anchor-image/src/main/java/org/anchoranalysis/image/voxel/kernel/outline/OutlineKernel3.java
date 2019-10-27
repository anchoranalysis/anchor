package org.anchoranalysis.image.voxel.kernel.outline;

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

// Keeps any on pixel that touches an off pixel, off otheriwse
public class OutlineKernel3 extends BinaryKernel {
	
	private boolean outsideAtThreshold = false;
	
	// Disconsiders anything outside the threshold. Takes priority ahead of  outsideAtThreshold
	private boolean ignoreAtThreshold = false;
	
	private boolean useZ;
	
	private BinaryValuesByte bv;
	
	private LocalSlices inSlices;
	
	private Extent extnt;
	
	// Constructor
	public OutlineKernel3(BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ) {
		super(3);
		this.outsideAtThreshold = outsideAtThreshold;
		this.useZ = useZ;
		this.bv = bv;
	}
	
	@Override
	public void init(VoxelBox<ByteBuffer> in) {
		this.extnt = in.extnt();
	}

	@Override
	public void notifyZChange(LocalSlices inSlices, int z) {
		this.inSlices = inSlices;
	}

	@Override
	public boolean accptPos( int ind, Point3i pnt ) {

		ByteBuffer inArr_Z = inSlices.getLocal(0);
		ByteBuffer inArr_ZLess1 = inSlices.getLocal(-1);
		ByteBuffer inArr_ZPlus1 = inSlices.getLocal(+1);
		
		int xLength = extnt.getX();
		
		int x = pnt.getX();
		int y = pnt.getY();
		
		if (bv.isOff(inArr_Z.get(ind))) {
			return false;
		}
		
		// We walk up and down in x
		x--;
		ind--;
		if (x>=0) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return true;
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return true;
			}
		}
		
		x += 2;
		ind += 2;
		if (x<extnt.getX()) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return true;
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return true;
			}
		}
		x--;
		ind--;
		
		
		// We walk up and down in y
		y--;
		ind -= xLength;
		if (y>=0) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return true;
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return true;
			}
		}
		
		y += 2;
		ind += (2*xLength);
		if (y<(extnt.getY())) {
			if (bv.isOff(inArr_Z.get(ind))) {
				return true;
			}
		} else {
			if (!ignoreAtThreshold && !outsideAtThreshold) {
				return true;
			}
		}
		y--;
		ind -= xLength;
		
		
		if (useZ) {
			
			if (inArr_ZLess1!=null) {
				if (bv.isOff(inArr_ZLess1.get(ind))) {
					return true;
				}
			} else {
				if (!ignoreAtThreshold && !outsideAtThreshold) {
					return true;
				}
			}
			
			if (inArr_ZPlus1!=null) {
				if (bv.isOff(inArr_ZPlus1.get(ind))) {
					return true;
				}
			} else {
				if (!ignoreAtThreshold && !outsideAtThreshold) {
					return true;
				}
			}
		}
		
		return false;
	}

	public boolean isIgnoreAtThreshold() {
		return ignoreAtThreshold;
	}

	public void setIgnoreAtThreshold(boolean ignoreAtThreshold) {
		this.ignoreAtThreshold = ignoreAtThreshold;
	}

}
