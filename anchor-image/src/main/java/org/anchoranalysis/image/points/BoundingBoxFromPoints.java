package org.anchoranalysis.image.points;

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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;

/**
 * Creates a bounding-box from one or more points
 * 
 * @author owen
 *
 */
public class BoundingBoxFromPoints {

	/**
	 * Create from a list of points
	 * 
	 * @param pnts the list
	 * @return a bounding-box minimally spanning all points
	 * @throws OperationFailedException if there are zero points
	 */
	public static BoundingBox forList( List<Point3i> pnts ) throws OperationFailedException {
		if (pnts.size()==0) {
			throw new OperationFailedException("Points list must contain at least one item");
		}
		
		PointRange range = new PointRange();
		pnts.forEach( pnt-> range.add(pnt) );
		
		return range.deriveBoundingBox();
	}
	
	/**
	 * Creates a bounding-box for two unordered points.
	 * 
	 * <p>By unordered, it means that no one point must have a value higher than another</p>
	 * 
	 * @param pnt1 first-point (arbitrary order)
	 * @param pnt2 second-point (arbitrary order)
	 * @return a bounding-box minally spanning the two points
	 */
	public static BoundingBox forTwoPoints( Point3d pnt1, Point3d pnt2) {
		Point3d min = calcMin( pnt1, pnt2 );
		Point3d max = calcMax( pnt1, pnt2 );
		return new BoundingBox(min,max);
	}
	
	
	private static Point3d calcMin( Point3d pnt1, Point3d pnt2 ) {
		Point3d pnt = new Point3d();
		pnt.setX( Math.min( pnt1.getX(), pnt2.getX() ));
		pnt.setY( Math.min( pnt1.getY(), pnt2.getY() ));
		pnt.setZ( Math.min( pnt1.getZ(), pnt2.getZ() ));
		return pnt;
	}
	
	private static Point3d calcMax( Point3d pnt1, Point3d pnt2 ) {
		Point3d pnt = new Point3d();
		pnt.setX( Math.max( pnt1.getX(), pnt2.getX() ));
		pnt.setY( Math.max( pnt1.getY(), pnt2.getY() ));
		pnt.setZ( Math.max( pnt1.getZ(), pnt2.getZ() ));
		return pnt;
	}
}
