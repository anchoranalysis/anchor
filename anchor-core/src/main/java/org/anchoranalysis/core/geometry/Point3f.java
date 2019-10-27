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


import java.io.Serializable;

import org.anchoranalysis.core.arithmetic.FloatUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Point3f implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float x = 0.0f;
	private float y = 0.0f;
	private float z = 0.0f;
	
	public Point3f() {
		// Initializes with [0, 0, 0]
	}
	
	public Point3f( Point3f pnt) {
		this.x = pnt.x;
		this.y = pnt.y;
		this.z = pnt.z;
	}
	
	public Point3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setValueByDimension( int dimIndex, float val ) {
		switch( dimIndex ) {
		case 0:
			this.x = val;
			break;
		case 1:
			this.y = val;
			break;
		case 2:
			this.z = val;
			break;
		default:
			assert false;
		}

	}
	
	public float getValueByDimension( int dimIndex ) {
		switch( dimIndex ) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			assert false;
			return 0;
		}
	}
	
	public float distanceSquared( Point3f pnt ) {
		float sx = this.x - pnt.x;
		float sy = this.y - pnt.y;
		float sz = this.z - pnt.z;
		return (sx*sx) + (sy*sy) + (sz*sz);
	}
	
	public double distance( Point3f pnt ) {
		return Math.sqrt( distanceSquared(pnt) );
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	@Override
	public String toString() {
		return String.format("[%f,%f,%f]",x,y,z);
	}
	
	@Override
	public boolean equals( Object obj ) {
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Point3f)) {
	        return false;
	    }
	    Point3f objCast = (Point3f) obj;
	    
	    if (!FloatUtilities.areEqual(x, objCast.x)) {
	    	return false;
	    }
	    
	    if (!FloatUtilities.areEqual(y, objCast.y)) {
	    	return false;
	    }
	    
	    if (!FloatUtilities.areEqual(z, objCast.z)) {
	    	return false;
	    }
	    
	    return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
	        .append(x)
	        .append(y)
	        .append(z)
	        .toHashCode();
	}
}
