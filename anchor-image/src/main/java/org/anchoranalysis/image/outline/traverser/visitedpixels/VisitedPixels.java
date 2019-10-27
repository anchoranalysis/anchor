package org.anchoranalysis.image.outline.traverser.visitedpixels;

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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.combine.CombineToOnePath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.combine.FindMinDistance;

/** All pixels that have been visited so far */
public class VisitedPixels {
	
	private List<ContiguousPixelPath> paths = new ArrayList<>();
	
	/** Adds a new path with a point */
	public void addNewPath( Point3i pnt, Point3i connPnt ) {
		ContiguousPixelPath path = new ContiguousPixelPath(pnt, connPnt);
		paths.add(path);
	}
	
	/** Adds a visited-point to an existing path, or else makes a new one */
	public void addVisitedPnt(Point3i pnt) {
		for( ContiguousPixelPath path : paths ) {
			if (path.maybeAddPntToClosestEnd(pnt)) {
				return;
			}
		}
		
		// No existing path can handle the point, so we make a new one
		//  we are not aware of any connection point in this case
		addNewPath(pnt, null);
	}
	
	/** The distMax of a point to the closest contiguous-path */
	public int distMaxToHeadTail(Point3i pnt) {
		return FindMinDistance.minDistMaxToHeadTail(pnt, paths);
	}
		
	/** Combines all the contiguous paths to a single-path 
	 * @throws OperationFailedException */
	public ContiguousPixelPath combineToOnePath( Extent extnt ) throws OperationFailedException {
		return CombineToOnePath.combineToOnePath(paths, extnt );
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ContiguousPixelPath path : paths) {
			sb.append(path.toString());
		}
		return sb.toString();
	}
}
