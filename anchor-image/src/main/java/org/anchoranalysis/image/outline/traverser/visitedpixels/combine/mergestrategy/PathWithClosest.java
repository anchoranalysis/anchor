package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

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
import org.anchoranalysis.image.outline.traverser.contiguouspath.DistanceToContiguousPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.LoopablePoints;

class PathWithClosest {
	
	private ContiguousPixelPath path;
	private int closest;
	
	public PathWithClosest(ContiguousPixelPath path, Point3i mergePnt ) {
		this.path = path;
		closest = indexClosest(path,mergePnt);
	}
	
	public LoopablePoints removeLeft() {
		return path.removeLeft(closest);
	}
	
	public LoopablePoints removeRight() {
		return path.removeRight(size()-closest-1);
	}
	
	public int distFromLeft() {
		return closest;
	}
	
	public int distFromRight() {
		return size() - closest - 1;
	}
	
	public int size() {
		return path.size();
	}
		
	private static int indexClosest(ContiguousPixelPath path, Point3i mergePnt) {
		return DistanceToContiguousPath.distMaxToClosestPoint(path, mergePnt).getIndex();
	}

	public List<Point3i> points() {
		return path.points();
	}

	public ContiguousPixelPath getPath() {
		return path;
	}

	public void insertBefore(List<Point3i> pts) {
		path.insertBefore(pts);
		closest += pts.size();
	}

	public void insertAfter(List<Point3i> pts) {
		path.insertAfter(pts);
	}
}
