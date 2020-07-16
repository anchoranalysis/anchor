package org.anchoranalysis.core.geometry;

import lombok.NoArgsConstructor;

/*-
 * #%L
 * anchor-core
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
@NoArgsConstructor
public final class Point3d extends Tuple3d {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Point3d( Point3f point) {
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	public Point3d( Vector3d point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}
	
	public Point3d( Tuple3d point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}
	
	public Point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Point3d midPointBetween(Point3d point1, Point3d point2) {
		// We create a new object of 1x1x1 between the two merged seeds
		Point3d pointNew = new Point3d(point1);
		pointNew.add( point2 );
		pointNew.scale(0.5);
		return pointNew;
	}
	
	public double distanceSquared( Point3d point ) {
		double sx = this.x - point.x;
		double sy = this.y - point.y;
		double sz = this.z - point.z;
		return (sx*sx) + (sy*sy) + (sz*sz);
	}
	
	public double distanceSquared( Point3i point ) {
		double sx = this.x - point.x;
		double sy = this.y - point.y;
		double sz = this.z - point.z;
		return (sx*sx) + (sy*sy) + (sz*sz);
	}
	
	public double distance( Point3d point ) {
		return Math.sqrt(
			distanceSquared(point)
		);
	}

	/**
	 * Element-wise minimum between this point and another
	 * 
	 * @param point the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3d min( Tuple3d point ) {
		return new Point3d(
			Math.min(x, point.x),
			Math.min(y, point.y),
			Math.min(z, point.z)
		);
	}
	
	/**
	 * Element-wise minimum between this point and another
	 * 
	 * @param point the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3d min( ReadableTuple3i point ) {
		return new Point3d(
			Math.min(x, point.getX()),
			Math.min(y, point.getY()),
			Math.min(z, point.getZ())
		);
	}

	/**
	 * Element-wise maximum between this point and a scalar
	 * 
	 * @param val the scalar
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3d max( double val ) {
		return new Point3d(
				Math.max(x, val),
				Math.max(y, val),
				Math.max(z, val)
			);
	}
	
	/**
	 * Element-wise maximum between this point and another
	 * 
	 * @param point the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3d max( Tuple3d point ) {
		return new Point3d(
			Math.max(x, point.x),
			Math.max(y, point.y),
			Math.max(z, point.z)
		);
	}
	
	public double l2norm() {
		return Math.sqrt( (x*x) + (y*y) + (z*z) );
	}

	@Override
	public boolean equals(Object obj) { // NOSONAR
		
		if (this == obj) {
			return true;
		}
		
		if( obj instanceof Point3d ) {
			return super.equals(obj);
		} else {
			return false;
		}
	}
	
	/** Performs an addition without changing any values in an existing point */
	public static Point3d immutableAdd(Tuple3d point, Tuple3d toAdd) {
		Point3d pointDup = new Point3d(point);
		pointDup.add(toAdd);
		return pointDup;
	}
	
	/** Performs a subtraction without changing any values in an existing point */
	public static Point3d immutableSubtract(Tuple3d point, Tuple3d toSubtract) {
		Point3d pointDup = new Point3d(point);
		pointDup.subtract(toSubtract);
		return pointDup;
	}
	
	/** Performs a scale without changing any values in an existing point */
	public static Point3d immutableScale(Tuple3d point, int factor) {
		Point3d pointDup = new Point3d(point);
		pointDup.scale(factor);
		return pointDup;
	}
}
