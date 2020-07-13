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

/**
 * Erosion with a 3x3 or 3x3x3 kernel
 * 
 * @author Owen Feehan
 *
 */
public final class ErosionKernel3 extends BinaryKernelMorph3Extent {
	
	// Constructor
	public ErosionKernel3(BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ) {
		super(bv, outsideAtThreshold, useZ);
	}

	/**
	 * This method is deliberately not broken into smaller pieces to avoid inlining.
	 * 
	 * <p>This efficiency matters as it is called so many times over a large image.</p>
	 * <p>Apologies that it is difficult to read with high cognitive-complexity.</p>
	 */
	@Override
	public boolean accptPos( int ind, Point3i pnt ) {

		ByteBuffer inArrZ = inSlices.getLocal(0);
		ByteBuffer inArrZLess1 = inSlices.getLocal(-1);
		ByteBuffer inArrZPlus1 = inSlices.getLocal(+1);
		
		int xLength = extent.getX();
		
		int x = pnt.getX();
		int y = pnt.getY();
		
		if (bv.isOff(inArrZ.get(ind))) {
			return false;
		}
		
		// We walk up and down in x
		x--;
		ind--;
		if (x>=0) {
			if (bv.isOff(inArrZ.get(ind))) {
				return false;
			}
		} else {
			if (outsideAtThreshold) {
				return false;
			}
		}
		
		x += 2;
		ind += 2;
		if (x<extent.getX()) {
			if (bv.isOff(inArrZ.get(ind))) {
				return false;
			}
		} else {
			if (outsideAtThreshold) {
				return false;
			}
		}
		ind--;
		
		
		// We walk up and down in y
		y--;
		ind -= xLength;
		if (y>=0) {
			if (bv.isOff(inArrZ.get(ind))) {
				return false;
			}
		} else {
			if (outsideAtThreshold) {
				return false;
			}
		}
		
		y += 2;
		ind += (2*xLength);
		if (y<(extent.getY())) {
			if (bv.isOff(inArrZ.get(ind))) {
				return false;
			}
		} else {
			if (outsideAtThreshold) {
				return false;
			}
		}
		ind -= xLength;
		
		
		if (useZ) {
			
			if (inArrZLess1!=null) {
				if (bv.isOff(inArrZLess1.get(ind))) {
					return false;
				}
			} else {
				if (outsideAtThreshold) {
					return false;
				}
			}
			
			if (inArrZPlus1!=null) {
				if (bv.isOff(inArrZPlus1.get(ind))) {
					return false;
				}
			} else {
				if (outsideAtThreshold) {
					return false;
				}
			}
		}
		
		return true;
	}

}
