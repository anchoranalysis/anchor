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
	public String toString() {
		return String.format("[%d,%d,%d]",x,y,z);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple3i other = (Tuple3i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
}
