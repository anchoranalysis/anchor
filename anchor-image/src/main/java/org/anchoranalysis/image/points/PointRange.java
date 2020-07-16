package org.anchoranalysis.image.points;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;

/** 
 * Keeps track of the range experienced by points in the x, y and z dimensions
 * 
 * <p>Points can be dynamically added and the min/max is updated continuously</p>
 * 
 * @author Owen Feehan
 * 
 *  */
public final class PointRange {

	private Point3i max;
	private Point3i min;
		
	public void add(Point3i point) {
		add(point.getX(), point.getY(), point.getZ());
	}
	
	public void add(int x, int y, int z) {
		
		if (max==null || min==null) {
			max = new Point3i(x,y,z);
			min = new Point3i(x,y,z);
			return;
		}
		
		if (x<min.getX()) {
			min.setX(x);
		} else if (x>max.getX()) {
			max.setX(x);
		}
		
		if (y<min.getY()) {
			min.setY(y);
		} else if (y>max.getY()) {
			max.setY(y);
		}
		
		if (z<min.getZ()) {
			min.setZ(z);
		} else if (z>max.getZ()) {
			max.setZ(z);
		}
	}
	
	public BoundingBox deriveBoundingBox() throws OperationFailedException {
		
		if (min==null || max==null) {
			throw new OperationFailedException("No point has been added, so no bounding-box can be derived");
		}
		
		return deriveBoundingBoxNoCheck();
	}
	
	/** 
	 * Like {@link #deriveBoundingBox} but doesn't throw an exception if no points exist.
	 * <p>
	 * Consider calling {@link #isEmpty} first to check.
	 */
	public BoundingBox deriveBoundingBoxNoCheck() {
		return new BoundingBox(min, max);
	}
	
	/** Has a valid min and max defined? In other words, has at least one point been added? */
	public boolean isEmpty() {
		return (min==null) || (max==null);
	}
}
