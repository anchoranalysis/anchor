package org.anchoranalysis.image.binary.values;

import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;

/*
 * #%L
 * anchor-image
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


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class BinaryValues {

	private final int offInt;
	private final int onInt;
	
	private static final BinaryValues defaultBB = new BinaryValues( 0, 255 );
	
	public BinaryValues(int off, int on) {
		this.offInt = off;
		this.onInt = on;
	}
	
	public BinaryValues(BinaryValues bv) {
		this.offInt = bv.offInt;
		this.onInt = bv.onInt;
	}
	
	public int getOffInt() {
		return offInt;
	}
	
	public int getOnInt() {
		return onInt;
	}

	public BinaryValuesByte createByte() {
		if (offInt>255) {
			throw new IncorrectVoxelDataTypeException("offInt must be <= 255");
		}
		if (onInt>255) {
			throw new IncorrectVoxelDataTypeException("onInt must be <= 255");
		}
		return new BinaryValuesByte(offInt, onInt);
	}

	public static BinaryValues getDefault() {
		return defaultBB;
	}
	
	public BinaryValues createInverted() {
		return new BinaryValues(onInt, offInt);
	}
	
	public BinaryValues duplicate() {
		return new BinaryValues(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   BinaryValues rhs = (BinaryValues) obj;
		   return new EqualsBuilder()
             .append(offInt, rhs.offInt)
             .append(onInt, rhs.onInt)
             .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(offInt)
			.append(onInt)
			.toHashCode();
	}
	
}
