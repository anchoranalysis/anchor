package org.anchoranalysis.core.geometry;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


public final class Point3i extends Tuple3i {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Point3i() {
		// Initializes wit zeroes
	}
	
	public Point3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3i( Tuple3i pnt) {
		this.x = pnt.x;
		this.y = pnt.y;
		this.z = pnt.z;
	}

	public Point3i( Tuple3d pnt) {
		this.x = (int) pnt.x;
		this.y = (int) pnt.y;
		this.z = (int) pnt.z;
	}

	@Override
	public boolean equals( Object obj ) { // NOSONAR
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Point3i)) {
	        return false;
	    }
	    return super.equals(obj);
	}
	
	/**
	 * Element-wise minimum between this point and another
	 * 
	 * @param pnt the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3i min( Tuple3i pnt ) {
		return new Point3i(
			Math.min(x, pnt.x),
			Math.min(y, pnt.y),
			Math.min(z, pnt.z)
		);
	}
	
	
	/**
	 * Element-wise maximum between this point and another
	 * 
	 * @param pnt the other point
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3i max( Tuple3i pnt ) {
		return new Point3i(
			Math.max(x, pnt.x),
			Math.max(y, pnt.y),
			Math.max(z, pnt.z)
		);
	}
	
	/**
	 * Element-wise maximum between this point and a scalar
	 * 
	 * @param val the scalar
	 * @return a new point containing the minimum of the x, y, z components
	 */
	public Point3i max( int val ) {
		return new Point3i(
			Math.max(x, val),
			Math.max(y, val),
			Math.max(z, val)
		);
	}
	
	public int distanceSquared( Point3i pnt ) {
		int sx = this.x - pnt.x;
		int sy = this.y - pnt.y;
		int sz = this.z - pnt.z;
		return (sx*sx) + (sy*sy) + (sz*sz);
	}
	
	public double distance( Point3i pnt ) {
		return Math.sqrt( distanceSquared(pnt) );
	}
	
	/** The maximum distance across any axis */
	public int distanceMax( Point3i pnt ) {
		int sx = Math.abs(this.x - pnt.x);
		int sy = Math.abs(this.y - pnt.y);
		int sz = Math.abs(this.z - pnt.z);
		return Math.max(sx, Math.max(sy, sz));
	}
}
