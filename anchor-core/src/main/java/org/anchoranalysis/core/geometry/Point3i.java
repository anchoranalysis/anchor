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
	
	public Point3i(ReadableTuple3i pnt) {
		this.x = pnt.getX();
		this.y = pnt.getY();
		this.z = pnt.getZ();
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
	
	@Override
	public Point3i duplicateChangeZ( int zNew ) {
		return new Point3i(
			x,
			y,
			zNew
		);
	}
	
	/** Performs an addition without changing any values in an existing point */
	public static Point3i immutableAdd(ReadableTuple3i point, ReadableTuple3i toAdd) {
		Point3i pntDup = new Point3i(point);
		pntDup.add(toAdd);
		return pntDup;
	}
	
	/** Performs a subtraction without changing any values in an existing point */
	public static Point3i immutableSubtract(ReadableTuple3i point, ReadableTuple3i toSubtract) {
		Point3i pntDup = new Point3i(point);
		pntDup.subtract(toSubtract);
		return pntDup;
	}
	
	/** Performs a scale without changing any values in an existing point */
	public static Point3i immutableScale(ReadableTuple3i pnt, int factor) {
		Point3i pntDup = new Point3i(pnt);
		pntDup.scale(factor);
		return pntDup;
	}
}
