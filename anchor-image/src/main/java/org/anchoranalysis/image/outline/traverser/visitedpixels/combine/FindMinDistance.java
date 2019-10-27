package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

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

import java.util.List;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.contiguouspath.DistanceIndex;
import org.anchoranalysis.image.outline.traverser.contiguouspath.DistanceToContiguousPath;

public class FindMinDistance {

	/** The distMax of a point to the closest contiguous-path */
	public static int minDistMaxToHeadTail(Point3i pnt, List<ContiguousPixelPath> paths ) {
		
		int distMin = Integer.MAX_VALUE;
		
		// Finds the minimum distance to any of the paths
		for( ContiguousPixelPath path : paths ) {
			int dist = DistanceToContiguousPath.distMaxToHeadTail(path, pnt);
			if (dist < distMin) {
				distMin = dist;
			}
		}
		
		return distMin;
	}
	
	/** The index of whichever of the ContiguousPixelPaths has the nearest point */
	public static DistanceIndexTwice indexDistMaxToClosestPoint(Point3i pnt, List<ContiguousPixelPath> paths, int avoidIndex ) {

		// The -1 is arbitrary
		DistanceIndex minDistance = new DistanceIndex(Integer.MAX_VALUE, -1);
		int indexWithMin = -1;
		
		// Finds the minimum distance to any of the paths
		for( int i=0; i<paths.size(); i++) {
			
			if (i==avoidIndex) {
				continue;
			}
			
			ContiguousPixelPath path = paths.get(i);
			
			DistanceIndex dist = DistanceToContiguousPath.distMaxToClosestPoint(path, pnt);
			if (dist.getDistance() < minDistance.getDistance()) {
				minDistance = dist;
				indexWithMin = i;
			}
		}
		
		return new DistanceIndexTwice(minDistance.getDistance(), indexWithMin, minDistance.getIndex());
	}
}
