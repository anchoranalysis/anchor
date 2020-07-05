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

import org.anchoranalysis.core.arithmetic.DoubleUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class Point2d implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double x;
	private double y;
	
	public Point2d() {
		x = 0;
		y = 0;
	}
	
	public Point2d( Point2d pnt ) {
		this.x = pnt.x;
		this.y = pnt.y;
	}
	
	public Point2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void add( Point2i pnt ) {
		this.x = this.x + pnt.getX();
		this.y = this.y + pnt.getY();
	}
	
	public void scale( double factor ) {
		this.x *= factor;
		this.y *= factor;
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

	public double distanceSquared( Point2d pnt ) {
		double sx = this.x - pnt.x;
		double sy = this.y - pnt.y;
		return (sx*sx) + (sy*sy);
	}
	
	public double distance( Point2d pnt ) {
		return Math.sqrt( distanceSquared(pnt) );
	}
	
	@Override
	public String toString() {
		return String.format("[%f,%f]",x,y);
	}
	
	@Override
	public boolean equals( Object obj ) {
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Point2d)) {
	        return false;
	    }
	    Point2d objCast = (Point2d) obj;
	    
	    if (!DoubleUtilities.areEqual(x, objCast.x)) {
	    	return false;
	    }
	    
	    return (DoubleUtilities.areEqual(y, objCast.y));
	}
	
	public Point2f toFloat() {
		return new Point2f(
			(float) this.x,
			(float) this.y
		);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
	        .append(x)
	        .append(y)
	        .toHashCode();
	}
}
