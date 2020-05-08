package org.anchoranalysis.core.geometry;

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

public final class Point3d extends Tuple3d {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Point3d() {
		super();
	}
	
	public Point3d( Point3f pnt) {
		this.x = pnt.getX();
		this.y = pnt.getY();
		this.z = pnt.getZ();
	}
	
	public Point3d( Vector3d pnt) {
		this.x = pnt.x;
		this.y = pnt.y;
		this.z = pnt.z;
	}
	
	public Point3d( Tuple3d pnt) {
		this.x = pnt.x;
		this.y = pnt.y;
		this.z = pnt.z;
	}
	
	public Point3d( Tuple3i pnt) {
		this.x = pnt.x;
		this.y = pnt.y;
		this.z = pnt.z;
	}
	
	public Point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Point3d midPointBetween(Point3d pnt1, Point3d pnt2) {
		// We create a new object of 1x1x1 between the two merged seeds
		Point3d pntNew = new Point3d(pnt1);
		pntNew.add( pnt2 );
		pntNew.scale(0.5);
		return pntNew;
	}
	
	public double distanceSquared( Point3d pnt ) {
		double sx = this.x - pnt.x;
		double sy = this.y - pnt.y;
		double sz = this.z - pnt.z;
		return (sx*sx) + (sy*sy) + (sz*sz);
	}
	
	public double distance( Point3d pnt ) {
		return Math.sqrt( distanceSquared(pnt) );
	}

	/**
	 * Element-wise minimum between this point and another
	 * 
	 * @param pnt the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3d min( Tuple3d pnt ) {
		return new Point3d(
			Math.min(x, pnt.x),
			Math.min(y, pnt.y),
			Math.min(z, pnt.z)
		);
	}
	
	/**
	 * Element-wise minimum between this point and another
	 * 
	 * @param pnt the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3d min( Tuple3i pnt ) {
		return new Point3d(
			Math.min(x, pnt.x),
			Math.min(y, pnt.y),
			Math.min(z, pnt.z)
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
	 * @param pnt the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3d max( Tuple3d pnt ) {
		return new Point3d(
			Math.max(x, pnt.x),
			Math.max(y, pnt.y),
			Math.max(z, pnt.z)
		);
	}
	
	public double l2norm() {
		return Math.sqrt( (x*x) + (y*y) + (z*z) );
	}
	
	public Point3f toFloat() {
		return new Point3f(
			(float) this.x,
			(float) this.y,
			(float) this.z
		);
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

}
