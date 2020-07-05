package org.anchoranalysis.image.voxel;

import org.anchoranalysis.image.convert.ByteConverter;

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


import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

/** A list of intensity values of voxels */
public final class VoxelIntensityList {

	private DoubleArrayList lst;

	public VoxelIntensityList() {
		lst = new DoubleArrayList();
	}
	
	public VoxelIntensityList( int initialCapacity ) {
		lst = new DoubleArrayList( initialCapacity );
	}
	
	public final void add( double val ) {
		lst.add(val);
	}
	
	public final void add( byte val ) {
		lst.add( ByteConverter.unsignedByteToInt(val) );
	}
	
	public final double sum() {
		return Descriptive.sum( lst ); 
	}

	public final double mean() {
		return Descriptive.mean( lst ); 
	}
	
	public final double variance( double mean ) {
		int size = lst.size();
		double sum = mean * size;
		return Descriptive.variance( size, sum, sumOfSquares() );
	}
	
	public final long sumOfSquares() {
		return (long) Descriptive.sumOfSquares(lst);
	}
	
	public final double standardDeviation( double variance ) {
		
		return Descriptive.standardDeviation( variance );
	}

	public int size() {
		return lst.size();
	}

	public double get(int arg0) {
		return lst.get(arg0);
	}

	public void addAllOf(VoxelIntensityList arg0) {
		lst.addAllOf(arg0.lst);
	}
	
}
