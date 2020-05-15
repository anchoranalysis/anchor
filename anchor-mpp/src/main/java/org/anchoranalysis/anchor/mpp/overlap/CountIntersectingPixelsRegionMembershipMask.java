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
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.intersecting.IntersectionBBox;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * Counts the number of intersecting-pixels where bytes are encoded as region memberships
 * 
 * @author FEEHANO
 *
 */
class CountIntersectingPixelsRegionMembershipMask {
	
	private byte regionMembershipFlag;
	
	public CountIntersectingPixelsRegionMembershipMask(byte regionMembershipFlag) {
		this.regionMembershipFlag = regionMembershipFlag;
	}
	
	public int countIntersectingPixelsMaskGlobal(
		BoundedVoxelBox<ByteBuffer> src,
		BoundedVoxelBox<ByteBuffer> other,
		VoxelBox<ByteBuffer> maskGlobal,
		byte onMaskGlobal
	) {
		return countCheckIntersection( src, other, src.getBoundingBox(), other.getBoundingBox(), maskGlobal, onMaskGlobal );
	}
	
	private int countCheckIntersection(
		BoundedVoxelBox<ByteBuffer> src,
		BoundedVoxelBox<ByteBuffer> other,
		BoundingBox srcBox,
		BoundingBox otherBox,
		VoxelBox<ByteBuffer> maskGlobal,
		byte onMaskGlobal
	) {
		
		// Find the common bounding box
		BoundingBox bboxIntersect = new BoundingBox( srcBox );
		
		if (!bboxIntersect.intersect( otherBox, true )) {
			// If the bounding boxes don't intersect then we can
			//   go home early
			return 0;
		}
		
		return countIntersectingPixelsFromBBoxMaskGlobal(
			src,
			other,
			bboxIntersect,
			maskGlobal,
			onMaskGlobal
		);
	}
	
	// count intersecting pixels, but only includes a pixel ifs marked as onMaskGlobal in the mask
	//   voxel buffer
	private int countIntersectingPixelsFromBBoxMaskGlobal(
		BoundedVoxelBox<ByteBuffer> src,
		BoundedVoxelBox<ByteBuffer> other,
		BoundingBox bboxIntersect,
		VoxelBox<ByteBuffer> maskGlobal,
		byte onMaskGlobal
	) {
		Extent eGlobalMask = maskGlobal.extnt();
		
		IntersectionBBox bbox = IntersectionBBox.create(
			src.getBoundingBox(),
			other.getBoundingBox(),
			bboxIntersect
		);
		
		// Otherwise we count the number of pixels that are not empty
		//  in both voxel boxes in the intersecting region
		int cnt = 0;
				
		for (int z=bbox.z().min(); z<bbox.z().max(); z++) {

			ByteBuffer buffer = src.getVoxelBox().getPixelsForPlane(z).buffer();
			
			int z_other = z + bbox.z().rel();
			int z_global = z + src.getBoundingBox().getCrnrMin().getZ();
			
			ByteBuffer bufferOther = other.getVoxelBox().getPixelsForPlane(z_other).buffer();
			ByteBuffer bufferMaskGlobal = maskGlobal.getPixelsForPlane(z_global).buffer();

			buffer.clear();
			bufferOther.clear();
			
			cnt += countIntersectingPixelsOnGlobalMask(
				buffer,
				bufferOther,
				bufferMaskGlobal,
				bbox,
				src.getBoundingBox().getCrnrMin().getX(),
				src.getBoundingBox().getCrnrMin().getY(),
				eGlobalMask,
				onMaskGlobal
			);
		}
					
		return cnt;		
	}
	
	private int countIntersectingPixelsOnGlobalMask(
		ByteBuffer buffer1,
		ByteBuffer buffer2,
		ByteBuffer bufferMaskGlobal,
		IntersectionBBox bbox,
		int xGlobalRel,
		int yGlobalRel,
		Extent extntGlobal,
		byte onMaskGlobal
	) {
		
		int cnt = 0;
		for (int y=bbox.y().min(); y<bbox.y().max(); y++) {
			int y_other = y + bbox.y().rel();
			int y_global = y + yGlobalRel;
			
			for (int x=bbox.x().min(); x<bbox.x().max(); x++) {
				int x_other = x + bbox.x().rel();
				int x_global = x + xGlobalRel;
				
				byte globalMask = bufferMaskGlobal.get( extntGlobal.offset(x_global, y_global) );
				if (globalMask==onMaskGlobal) {
				
					byte posCheck = buffer1.get( bbox.e1().offset(x, y) );
					byte posCheckOther = buffer2.get( bbox.e2().offset(x_other, y_other) );
					
					if ( isPixelInRegion(posCheck) && isPixelInRegion(posCheckOther) ) {
						cnt++;
					}
				}
			}
		}
		return cnt;
	}
		
	private boolean isPixelInRegion( byte pixelVal ) {
		return RegionMembershipUtilities.isMemberFlagAnd(pixelVal, regionMembershipFlag);
	}
}
