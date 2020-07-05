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

public final class Point2f implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float x;
	private float y;

	/**
	 * Initialized with zeros
	 */
	public Point2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public Point2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void scale( double factor ) {
		this.x *= factor;
		this.y *= factor;
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

	@Override
	public String toString() {
		return String.format("[%f,%f]",x,y);
	}
	
	@Override
	public boolean equals( Object obj ) {
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Point2f)) {
	        return false;
	    }
	    Point2f objCast = (Point2f) obj;
	    
	    if (!FloatUtilities.areEqual(x, objCast.x)) {
	    	return false;
	    }
	    
	    return FloatUtilities.areEqual(y, objCast.y);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
	        .append(x)
	        .append(y)
	        .toHashCode();
	}
}
