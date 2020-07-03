package org.anchoranalysis.anchor.mpp.overlap;

/*
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.anchor.mpp.pxlmark.PxlMark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class OverlapUtilities {

	private OverlapUtilities() {}
	
	public static double overlapWith( PxlMarkMemo pmm1, PxlMarkMemo pmm2, int regionID ) {

		// If we have a quick routine available, we use this
		if (pmm2.getMark().hasOverlapWithQuick()) {
			return pmm1.getMark().overlapWithQuick( pmm2.getMark(), regionID );	
		}
		
		// We do some quick tests to get rid of obvious cases which do not overlap
		if ( pmm1.getMark().quickTestNoOverlap( pmm2.getMark(), regionID) ) {
			return 0.0;
		}
			
		// Otherwise we do it the slow way by seeing if any pixels intersect
		// between the two bounding box
		PxlMark mark1 = pmm1.doOperation(); 
		PxlMark mark2 = pmm2.doOperation();
		
		byte flag = RegionMembershipUtilities.flagForRegion(regionID);
		return new CountIntersectingPixelsRegionMembership(flag).countIntersectingPixels(
			mark1.getVoxelBox(),
			mark2.getVoxelBox()
		);
	}
	
	
	/**
	 * Counts the number of overlapping voxels between two {@link PxlMarkMemo}
	 * 
	 * @param pmm1
	 * @param pmm2
	 * @param regionID
	 * @param globalMask
	 * @param onGlobalMask
	 * @return the total number of overlapping boxels
	 */
	public static double overlapWithMaskGlobal(
		PxlMarkMemo pmm1,
		PxlMarkMemo pmm2,
		int regionID,
		VoxelBox<ByteBuffer> globalMask,
		byte onGlobalMask
	) {

		// If we have a quick routine available, we use this
		if (pmm2.getMark().hasOverlapWithQuick()) {
			return pmm1.getMark().overlapWithQuick( pmm2.getMark(), regionID );	
		}
		
		// We do some quick tests to get rid of obvious cases which do not overlap
		if ( pmm1.getMark().quickTestNoOverlap( pmm2.getMark(), regionID) ) {
			return 0.0;
		}
			
		// Otherwise we do it the slow way by seeing if any pixels intersect
		// between the two bounding box
		PxlMark mark1 = pmm1.doOperation(); 
		PxlMark mark2 = pmm2.doOperation();
		
		byte flag = RegionMembershipUtilities.flagForRegion(regionID);
		return new CountIntersectingPixelsRegionMembershipMask(flag).countIntersectingPixelsMaskGlobal(
			mark1.getVoxelBox(),
			mark2.getVoxelBox(),
			globalMask,
			onGlobalMask
		);
	}
}
