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
import org.anchoranalysis.core.axis.AxisType;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class Tuple3d implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	protected double x = 0.0;
	protected double y = 0.0;
	protected double z = 0.0;
	
	public void add( Point3d pnt ) {
		this.x += pnt.x;
		this.y += pnt.y;
		this.z += pnt.z;
	}
	
	public void sub( Point3d pnt ) {
		this.x -= pnt.x;
		this.y -= pnt.y;
		this.z -= pnt.z;
	}
	
	public void scale( double factor ) {
		this.x *= factor;
		this.y *= factor;
		this.z *= factor;
	}
	
	public void scaleXY( double factor ) {
		this.x *= factor;
		this.y *= factor;
	}

	public void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
		this.z = Math.abs(this.z);
	}
		
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public double get( AxisType axisType ) {
		switch( axisType ) {
		case X:
			return getX();
		case Y:
			return getY();
		case Z:
			return getZ();
		default:
			assert false;
			return Double.NaN;
		}
	}
	
	public double dot( Tuple3d vec ) {
		return (x*vec.x) + (y*vec.y) + (z*vec.z);
	}
	
	@Override
	public String toString() {
		return String.format("[%5.1f,%5.1f,%5.1f]",x,y,z);
	}
	
	@Override
	public boolean equals( Object obj ) {
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Tuple3d)) {
	        return false;
	    }
	    Tuple3d objCast = (Tuple3d) obj;
	    
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
	
	public void setX(int x) {
		this.x = (double) x;
	}
	
	public void setY(int y) {
		this.y = (double) y;
	}
	
	public void setZ(int z) {
		this.z = (double) z;
	}
}
