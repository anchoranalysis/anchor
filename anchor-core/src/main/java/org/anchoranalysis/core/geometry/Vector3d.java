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


public class Vector3d extends Tuple3d {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Vector3d() {
		// Initializes with zeroes
	}
	
	public Vector3d( Tuple3d tuple ) {
		this.x = tuple.x;
		this.y = tuple.y;
		this.z = tuple.z;
	}
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double length() {
		double squared = (x*x) + (y*y) + (z*z);
		return Math.sqrt(squared);
	}
	
	public void normalize() {
		double length = length();
		this.x /= length;
		this.y /= length;
		this.z /= length;
	}
	

	
	// Cross product
	public static Vector3d cross( Vector3d u, Vector3d v ) {
		Vector3d out = new Vector3d();
		out.x = u.getY()*v.getZ() - u.getZ()*v.getY();
		out.y = u.getZ()*v.getX() - u.getX()*v.getZ();
		out.z = u.getX()*v.getY() - u.getY()*v.getX();
		return out;
	}
	
	@Override
	public boolean equals( Object obj ) { // NOSONAR
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Vector3d)) {
	        return false;
	    }
	    return super.equals(obj);
	}
}
