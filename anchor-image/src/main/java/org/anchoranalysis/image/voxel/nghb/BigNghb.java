package org.anchoranalysis.image.voxel.nghb;

import org.anchoranalysis.image.voxel.iterator.changed.ProcessChangedPoint;

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



// 8-connectivity or 26-connectivity
// Relies on IProcessRelPoint to do bound checking
public class BigNghb extends Nghb {
	
	private boolean includeCentrePoint = false;
	
	public BigNghb() {
		
	}
	
	public BigNghb( boolean includeCentrePoint ) {
		this.includeCentrePoint = includeCentrePoint;
	}
	
	@Override
	public void processAllPointsInNghb(boolean do3D, ProcessChangedPoint testNghb) {
		
		if (do3D) {
			
			for (int z=-1; z<=1; z++) {
				
				if (!testNghb.notifyChangeZ(z)) {
					continue;
				}
				
				for (int y=-1; y<=1; y++) {
					for (int x=-1; x<=1; x++) {
						if (includeCentrePoint || x!=0 || y!=0 || z!=0) {
							testNghb.processPoint( x, y );
						}
					}
				}
			}
			
		} else {
			
			if (!testNghb.notifyChangeZ(0)) {
				return;
			}
			
			for (int y=-1; y<=1; y++) {
				for (int x=-1; x<=1; x++) {
					if (includeCentrePoint || x!=0 || y!=0 ) {
						testNghb.processPoint(x, y );
					}
				}
			}
		}
	}
}