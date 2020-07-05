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
		
		double distanceSquared2to1 = endPoint.distanceSquared(startPoint);
		double distanceSquared1to0 = startPoint.distanceSquared(pt);
		
		// Let's calculation the dot_product
		double firstX = startPoint.getX() - pt.getX();
		double firstY = startPoint.getY() - pt.getY();
		double firstZ = startPoint.getZ() - pt.getZ();
		
		double dotProduct = 
		  (firstX * directionVector.getX())
		  + (firstY * directionVector.getY())
		  + (firstZ * directionVector.getZ());
		
		double num = (distanceSquared2to1 * distanceSquared1to0) - Math.pow(dotProduct, 2);
		return num / distanceSquared2to1;
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
