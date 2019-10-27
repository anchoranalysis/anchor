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

import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class Tuple3i implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int x = 0;
	protected int y = 0;
	protected int z = 0;
	
	public void add( Tuple3i pnt ) {
		this.x = this.x + pnt.x;
		this.y = this.y + pnt.y;
		this.z = this.z + pnt.z;
	}
	
	public void sub( int val ) {
		this.x = this.x - val;
		this.y = this.y - val;
		this.z = this.z - val;
	}
	
	public void sub( Tuple3i pnt ) {
		this.x = this.x - pnt.x;
		this.y = this.y - pnt.y;
		this.z = this.z - pnt.z;
	}
	
	public void scale( int factor ) {
		this.x = this.x * factor;
		this.y = this.y * factor;
		this.z = this.z * factor;
	}
	
	public void div( int factor ) {
		this.x = this.x / factor;
		this.y = this.y / factor;
		this.z = this.z / factor;
	}
	
	public void scaleXY( double factor ) {
		this.x = (int) (factor * this.x);
		this.y = (int) (factor * this.y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public void incrX() {
		this.x++;
	}
	
	public void incrY() {
		this.y++;
	}
	
	public void incrZ() {
		this.z++;
	}
	
	public void incrX(int val) {
		this.x += val;
	}
	
	public void incrY(int val) {
		this.y += val;
	}
	
	public void incrZ(int val) {
		this.z += val;
	}
	
	public void decrX() {
		this.x--;
	}
	
	public void decrY() {
		this.y--;
	}
	
	public void decrZ() {
		this.z--;
	}
	
	public void decrX(int val) {
		this.x -= val;
	}
	
	public void decrY(int val) {
		this.y -= val;
	}
	
	public void decrZ(int val) {
		this.z -= val;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Tuple3i)) {
	        return false;
	    }
	    Tuple3i objCast = (Tuple3i) obj;
	    if (x!=objCast.x) {
	    	return false;
	    }
	    if (y!=objCast.y) {
	    	return false;
	    }
	    if (z!=objCast.z) {
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

	@Override
	public String toString() {
		return String.format("[%d,%d,%d]",x,y,z);
	}
}
