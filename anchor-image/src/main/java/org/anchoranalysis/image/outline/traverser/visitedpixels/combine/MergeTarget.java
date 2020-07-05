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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;

/**
 * A path and a location onto which a merge can occur, and the index of the vertice
 *  in the original list
 * 
 * @author Owen Feehan
 *
 */
class MergeTarget {
	
	private ContiguousPixelPath path;
	private int indexPath;
	private Point3i mergePnt;
	private int mergeIndex;
	
	public MergeTarget(ContiguousPixelPath path, int indexPath, int mergeIndex) {
		super();
		this.path = path;
		this.indexPath = indexPath;
		this.mergeIndex = mergeIndex;
	}		

	public ContiguousPixelPath getPath() {
		return path;
	}
	
	public int getIndexPath() {
		return indexPath;
	}
	
	public Point3i mergePnt() {
		return path.get(mergeIndex);
	}

	@Override
	public String toString() {
		return String.format(
			"path=%s pnt=%s indexPath=%d%n",
			path,
			mergePnt,
			indexPath
		);
	}
}
