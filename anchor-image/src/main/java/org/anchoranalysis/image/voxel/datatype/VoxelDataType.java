package org.anchoranalysis.image.voxel.datatype;

import org.apache.commons.lang.builder.HashCodeBuilder;

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


// The type of data contained within the channel
public abstract class VoxelDataType {
	
	private int numBits;
	private String typeIdentifier;
	private long maxValue;
	private long minValue;
	
	protected VoxelDataType(int numBits, String typeIdentifier, long maxValue, long minValue) {
		this.numBits = numBits;
		this.typeIdentifier = typeIdentifier;
		this.maxValue = maxValue;
		this.minValue = minValue;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof VoxelDataType)) {
			return false;
		}
		
		VoxelDataType objC = (VoxelDataType) obj;
		
		if (isInteger()!=objC.isInteger()) {
			return false;
		}
		
		if (isUnsigned()!=objC.isUnsigned()) {
			return false;
		}
		
		if (numBits()!=objC.numBits()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append( isInteger() )
			.append( isUnsigned() )
			.append( numBits() )
			.toHashCode();
	}
	
	public abstract boolean isInteger();
	
	public abstract boolean isUnsigned();

	public final int numBits() {
		return numBits;
	}
	
	@Override
	public final String toString() {
		return typeIdentifier;
	}

	public final long maxValue() {
		return maxValue;
	}
	
	public final long minValue() {
		return minValue;
	}
}
