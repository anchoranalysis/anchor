package org.anchoranalysis.anchor.mpp.overlap;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.image.object.intersecting.CountIntersectingVoxels;
import org.anchoranalysis.image.object.intersecting.IntersectionBBox;

import lombok.AllArgsConstructor;

/**
 * Counts the number of intersecting pixels where each buffer is encoded as region-membership
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
class CountIntersectingVoxelsRegionMembership extends CountIntersectingVoxels {

	private final byte regionMembershipFlag;
	
	@Override
	protected int countIntersectingVoxels(
		ByteBuffer buffer1,
		ByteBuffer buffer2,
		IntersectionBBox bbox
	) {
		
		int cnt = 0;
		for (int y=bbox.y().min(); y<bbox.y().max(); y++) {
			int yOther = y + bbox.y().rel();
			
			for (int x=bbox.x().min(); x<bbox.x().max(); x++) {
				int xOther = x + bbox.x().rel();
				
				byte posCheck = buffer1.get( bbox.e1().offset(x, y) );
				byte posCheckOther = buffer2.get( bbox.e2().offset(xOther, yOther) );
				
				if ( isPixelInRegion(posCheck) && isPixelInRegion(posCheckOther) ) {
					cnt++;
				}
			}
		}
		return cnt;
	}
	
	private boolean isPixelInRegion( byte pixelVal ) {
		return RegionMembershipUtilities.isMemberFlagAnd(pixelVal, regionMembershipFlag);
	}
}
