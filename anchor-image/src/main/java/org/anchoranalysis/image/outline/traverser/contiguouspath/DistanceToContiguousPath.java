package org.anchoranalysis.image.outline.traverser.contiguouspath;

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

import org.anchoranalysis.core.geometry.Point3i;

public class DistanceToContiguousPath {

	private DistanceToContiguousPath() {}
	
	/** The distMax of a point to the closest point on the path */
	public static DistanceIndex distMaxToClosestPoint(ContiguousPixelPath path, Point3i pnt ) {
		
		int indexMin = -1;
		int distMin = Integer.MAX_VALUE;
		
		// Finds the minimum distance to any of the paths
		for( int i=0; i<path.size(); i++ ) {
			
			Point3i pnt2 = path.get(i);
			
			int dist = pnt.distanceMax(pnt2);
			if (dist < distMin) {
				distMin = dist;
				indexMin = i;
			}
		}
		
		return new DistanceIndex(distMin,indexMin);
	}
		
	public static int distMaxToHeadTail(ContiguousPixelPath path, Point3i pnt) {
		return Math.min(
			pnt.distanceMax(path.head()),
			pnt.distanceMax(path.tail())
		);
	}
}
