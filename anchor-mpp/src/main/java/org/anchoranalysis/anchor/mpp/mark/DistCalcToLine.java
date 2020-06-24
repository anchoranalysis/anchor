package org.anchoranalysis.anchor.mpp.mark;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.core.geometry.Point3d;

class DistCalcToLine {
	
	private Point3d startPoint;
	private Point3d endPoint;
	private Point3d directionVector;
	
	public DistCalcToLine() {
		super();
	}
	
	public void setPoints( Point3d startPoint, Point3d endPoint ) {
		this.startPoint = new Point3d(startPoint);
		this.endPoint = new Point3d(endPoint);
		
		// Direction vector
		this.directionVector = new Point3d( this.endPoint );
		this.directionVector.subtract( startPoint );
	}

	public double distToLine( Point3d pt ) {
		// http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
		
		double x2_x1_dist_sq = endPoint.distanceSquared(startPoint);
		double x1_x0_dist_sq = startPoint.distanceSquared(pt);
		
		// Let's calculation the dot_product
		double first_x = startPoint.getX() - pt.getX();
		double first_y = startPoint.getY() - pt.getY();
		double first_z = startPoint.getZ() - pt.getZ();
		
		double dot_prod = 
		  (first_x * directionVector.getX())
		  + (first_y * directionVector.getY())
		  + (first_z * directionVector.getZ());
		
		double num = (x2_x1_dist_sq * x1_x0_dist_sq) - Math.pow(dot_prod, 2);
		return num / x2_x1_dist_sq;
	}

	public Point3d getStartPoint() {
		return startPoint;
	}

	public Point3d getEndPoint() {
		return endPoint;
	}

	public Point3d getDirectionVector() {
		return directionVector;
	}
}
