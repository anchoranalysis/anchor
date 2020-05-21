package org.anchoranalysis.image.voxel.iterator.changed;

import org.anchoranalysis.core.geometry.Point3i;

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

import org.anchoranalysis.image.extent.Extent;

/**
 * Only processes points within a certain extent
 * 
 * <p>Any points outside this extent are rejected.</p>
 * 
 * @author Owen Feehan
 *
 */
final class WithinExtent implements InitializableProcessChangedPoint {

	private final Extent extnt;
	private final ProcessChangedPointAbsolute processAbsolutePoint;
	
	private Point3i pnt;
	
	public WithinExtent( Extent extnt, ProcessChangedPointAbsolute processAbsolutePoint ) {
		this.extnt = extnt;
		this.processAbsolutePoint = processAbsolutePoint;
	}

	@Override
	public void initPnt(Point3i pnt) {
		this.pnt = pnt;			
	}
	
	@Override
	public boolean processPoint(int xChange, int yChange) {
		
		int x1 = pnt.getX() + xChange;
		int y1 = pnt.getY() + yChange;
		
		if (x1 < 0 || x1 >=extnt.getX() || y1 < 0 || y1 >= extnt.getY()) {
			return false;
		}
		
		return processAbsolutePoint.processPoint(xChange, yChange, x1, y1);
	}

	@Override
	public boolean notifyChangeZ(int zChange) {
		int z1 = pnt.getZ() + zChange;
		
		if (!extnt.containsZ(z1)) {
			return false;
		}
		
		processAbsolutePoint.notifyChangeZ(zChange, z1);
		return true;
	}

}
