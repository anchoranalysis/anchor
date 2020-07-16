package org.anchoranalysis.image.outline.traverser;

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

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.visitedpixels.VisitedPixels;

/** A priority-queue of what pixels to visit next */
class PriorityQueueVisit {
	
	private List<Point3iWithDistance> next;
	
	public PriorityQueueVisit() {
		next = new ArrayList<>();
	}
	
	public Point3iWithDistance pop( VisitedPixels visitedPixels ) {
		// Find point which is closest to the end of the visited pixels
		// We prioritise visits in this order to keep the chain as connected as possible
		int index = findIndexWithMinDistance(next, visitedPixels);
		if (index==-1) {
			// Special circumstance that can only occur when there's a single point
			assert(next.size()==1);
			index = 0;
		}
		return next.remove(index);
	}

	public void addAll(List<Point3iWithDistance> list) {
		for( Point3iWithDistance point : list ) {
			add(point);
		}
	}
	
	public void addAllWithConn(List<Point3iWithDistance> list, Point3i connPoint) {
		for( Point3iWithDistance point : list ) {
			point.markAsNewPath(connPoint);
			add(point);
		}
	}
	
	public boolean add(Point3iWithDistance arg0) {
		return next.add(arg0);
	}
	
	public boolean isEmpty() {
		return next.isEmpty();
	}
	
	private static int findIndexWithMinDistance( List<Point3iWithDistance> next, VisitedPixels visitedPixels ) {
		
		int distanceMin = Integer.MAX_VALUE;
		int index = -1;
		
		for( int i=0; i<next.size(); i++ ) {
			
			Point3iWithDistance point = next.get(i);
			
			int distance =  visitedPixels.distanceMaxToHeadTail(point.getPoint());
			
			if (distance < distanceMin) {
				index = i;
				distanceMin = distance;
			}
		}
		
		return index;
	}
}
