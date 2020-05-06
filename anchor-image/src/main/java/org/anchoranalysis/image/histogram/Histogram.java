package org.anchoranalysis.image.histogram;

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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.math.statistics.VarianceCalculator;

public abstract class Histogram {

	public abstract Histogram duplicate();
	
	public abstract void reset();

	public abstract void zeroVal( int val );
	
	public abstract void transferVal( int srcVal, int destVal );
	
	public abstract void incrVal( int val );
	
	public abstract void incrValBy( int val, int increase );
	
	public abstract void incrValBy( int val, long increase );
	
	public abstract boolean isEmpty();
	
	public abstract int getCount( int val );
	
	public abstract int size();
	
	public abstract void addHistogram( Histogram other ) throws OperationFailedException;
	
	public abstract double mean() throws OperationFailedException;
	
	/** calculates the mean after raising each histogram value to a power i.e. mean of histogramVal^power */
	public abstract double mean( double power ) throws OperationFailedException;
	
	/** calculates the mean of (histogramVal - subtractVal)^power */
	public abstract double mean( double power, double subtractVal ) throws OperationFailedException;
			
	public abstract double meanGreaterEqualTo( int val ) throws OperationFailedException;
			
	public abstract double meanNonZero() throws OperationFailedException;
	
	public abstract long sumNonZero();
	
	public abstract void scaleBy( double factor );
			
	public abstract int quantile( double quantile ) throws OperationFailedException;
			
	public abstract int quantileAboveZero( double quantile ) throws OperationFailedException;
	
	public abstract boolean hasAboveZero();
	
	public abstract double percentGreaterEqualTo( int intensity );
	
	public int calcMode() throws OperationFailedException {
		return calcMode(0);
	}
	
	// Should only be called on a histogram with at least one item
	public abstract int calcMode( int startIndex ) throws OperationFailedException;

	// Should only be called on a histogram with at least one item
	public abstract int calcMax() throws OperationFailedException;

	// Should only be called on a histogram with at least one item
	public abstract int calcMin() throws OperationFailedException;

	public abstract long calcSum();
	
	public abstract long calcSumSquares();
	
	public abstract long calcSumCubes();
	
	public abstract void removeBelowThreshold( int threshold );
	
	public abstract int calcNumNonZero();
	

	public double stdDev() throws OperationFailedException {
		return Math.sqrt( variance() );
	}
	
	public double variance() throws OperationFailedException {
		return new VarianceCalculator( calcSum(), calcSumSquares(),  getTotalCount() ).variance();
	}
	
	public abstract long countThreshold(RelationToValue relationToThreshold, double threshold);
	
	// The value split becomes part of the higher histogram
	public abstract HistogramsAfterSplit splitAt( int split );	
	
	// Thresholds (generates a new histogram, existing object is unchanged)
	public abstract Histogram threshold(RelationToValue relationToThreshold, double threshold);
	
	public abstract String toString();
	
	public String csvString() {
		StringBuilder sb = new StringBuilder();
		for (int t=0; t<=getMaxBin(); t++) {
			sb.append( String.format("%d, %d%n", t, getCount(t)) );
		}
		return sb.toString();
	}

	public abstract int getMaxBin();
	
	public abstract int getMinBin();

	public abstract long getTotalCount();
	
	public abstract Histogram extractPixelsFromRight( long numPixels );
	
	public abstract Histogram extractPixelsFromLeft( long numPixels );
}