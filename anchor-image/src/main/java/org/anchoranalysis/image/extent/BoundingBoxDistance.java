package org.anchoranalysis.image.extent;

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

import org.anchoranalysis.core.geometry.ReadableTuple3i;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Calculates distance between two bounding boxes
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class BoundingBoxDistance {
	
	public static double distance( BoundingBox box1, BoundingBox box2, boolean includeZ ) {
		
		if (box1.intersection().existsWith(box2)) {
			return 0;
		}
		
		ReadableTuple3i box1CornerMin = box1.cornerMin();
		ReadableTuple3i box1CornerMax = box1.calcCornerMax();
		
		ReadableTuple3i box2CornerMin = box2.cornerMin();
		ReadableTuple3i box2CornerMax = box2.calcCornerMax();
		
		int xDistance = minDistance(
			box1CornerMin.getX(),
			box1CornerMax.getX(),
			box2CornerMin.getX(),
			box2CornerMax.getX()
		);
		
		int yDistance = minDistance(
			box1CornerMin.getY(),
			box1CornerMax.getY(),
			box2CornerMin.getY(),
			box2CornerMax.getY()
		);
		
		int zDistance = 0;
		
		if (includeZ) {
			zDistance = minDistance(
				box1CornerMin.getZ(),
				box1CornerMax.getZ(),
				box2CornerMin.getZ(),
				box2CornerMax.getZ()
			);
		}
		
		return Math.sqrt( Math.pow(xDistance,2) + Math.pow(yDistance,2) + Math.pow(zDistance,2));
	}
	
	// We have already guaranteed no intersection
	private static int minDistance( int box1Min, int box1Max, int box2Min, int box2Max ) {
		
		// Consider intersections
		if( (box2Min >= box1Min) && (box2Min <= box1Max)) {
			return 0;
		}
		if( (box1Min >= box2Min) && (box1Min <= box2Max)) {
			return 0;
		}
		if( (box2Max >= box1Min) && (box2Max <= box1Max)) {
			return 0;
		}
		if( (box1Max >= box2Min) && (box1Max <= box2Max)) {
			return 0;
		}
				
		int diff1 = Math.abs(box1Min-box2Max); 
		int diff2 = Math.abs(box2Min-box1Max);
		int diff3 = Math.abs(box1Min-box2Min);
		int diff4 = Math.abs(box1Max-box2Max);
		return Math.min(
			Math.min(diff1, diff2),
			Math.min(diff3, diff4)
		);
	}
}
