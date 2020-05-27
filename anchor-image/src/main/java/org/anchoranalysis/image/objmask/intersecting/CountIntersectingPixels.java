package org.anchoranalysis.image.objmask.intersecting;

/*-
 * #%L
 * anchor-image
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
import java.util.Optional;

import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;

public abstract class CountIntersectingPixels {
	
	/**
	 * Calculates the number of intersecting pixels between two voxel boxes
	 * 
	 * @param src
	 * @param other
	 * @param onMaskThis a region membership mask or a binary-high value depending on region-membership
	 * @param onMaskOther a region membership mask or a binary-high value depending on region-membership
	 * @return
	 */
	public int countIntersectingPixels(
		BoundedVoxelBox<ByteBuffer> src,
		BoundedVoxelBox<ByteBuffer> other
	) {
		// Find the common bounding box
		Optional<BoundingBox> bboxIntersect = src.getBoundingBox().intersection().with(
			other.getBoundingBox()
		);
		
		if (!bboxIntersect.isPresent()) {
			// If the bounding boxes don't intersect then we can
			//   go home early
			return 0;
		}
		
		return countIntersectingPixelsFromBBox( src, other, bboxIntersect.get() );
	}
	
	// count intersecting pixels
	private int countIntersectingPixelsFromBBox(
		BoundedVoxelBox<ByteBuffer> src,
		BoundedVoxelBox<ByteBuffer> other,
		BoundingBox bboxIntersect
	) {
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
			ByteBuffer bufferOther = other.getVoxelBox().getPixelsForPlane(z_other).buffer();
							
			cnt += countIntersectingPixels(
				buffer,
				bufferOther,
				bbox
			);
		}
			
		return cnt;		
	}
	
	protected abstract int countIntersectingPixels(
		ByteBuffer buffer1,
		ByteBuffer buffer2,
		IntersectionBBox bbox
	);
}
