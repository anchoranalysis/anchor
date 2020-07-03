package org.anchoranalysis.image.histogram;

import static java.lang.Math.toIntExact;
import java.util.Arrays;
import java.util.function.Function;

import org.anchoranalysis.bean.shared.relation.GreaterThanBean;
import org.anchoranalysis.bean.shared.relation.LessThanBean;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToConstant;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;

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
import org.apache.commons.lang.ArrayUtils;

public class HistogramArray extends Histogram {

	private int[] arr;
	
	// By default the minimum bin value is always 0
	private int minBinVal;	// inclusive
	private int maxBinVal;	// inclusive
	private long cnt = 0;
	
	public HistogramArray( int maxBinVal ) {
		this(0, maxBinVal);
	}
	
	public HistogramArray( int minBinVal, int maxBinVal ) {
		arr = new int[maxBinVal-minBinVal+1];
		cnt = 0;
		this.maxBinVal = maxBinVal;
		this.minBinVal = minBinVal;
	}
	
	@Override
	public HistogramArray duplicate() {
		HistogramArray out = new HistogramArray(minBinVal,maxBinVal);
		out.arr = ArrayUtils.clone(arr);
		out.cnt = cnt;
		return out;
	}
	
	public void reset() {
		cnt = 0;
		for( int i=minBinVal;i<=maxBinVal;i++) {
			arrSet(i,0);
		}
	}
	
	public void zeroVal( int val ) {
		int indx = index(val);
		cnt -= arr[ indx ];
		arr[ indx ] = 0;
	}
	
	public void transferVal( int srcVal, int destVal ) {
		int srcIndx = index(srcVal);
	
		arrIncr(destVal, arr[srcIndx] );
		arr[srcIndx] = 0;
	}
	
	public void incrVal( int val ) {
		arrIncr(val, 1);
		cnt++;
	}
	
	public void incrValBy( int val, int increase ) {
		arrIncr(val, increase);
		cnt+=increase;
	}
	
	/**
	 * long version of incrValBy
	 * 
	 * @throws ArithmeticException if incresae cannot be converted to an int safely
	 */
	public void incrValBy( int val, long increase ) throws ArithmeticException {
		incrValBy( val, toIntExact(increase) );
	}
	
	@Override
	public void removeBelowThreshold( int threshold ) {
		for( int i=getMinBin(); i<threshold; i++) {
			zeroVal(i);
		}
		// Now chop off the unneeded values and set a new minimum
		chopArrBefore( index(threshold) );
		this.minBinVal = threshold;
	}
	
	public boolean isEmpty() {
		return cnt==0;
	}
	
	public int getCount( int val ) {
		return arrGet(val);
	}
	
	/** The number of items in the histogram (maxBinVal - minBinVal + 1) */
	public int size() {
		return arr.length;
	}
	
	public void addHistogram( Histogram other ) throws OperationFailedException {
		if (this.getMaxBin()!=other.getMaxBin()) {
			throw new OperationFailedException("Cannot add histograms with different max-bin-values");
		}
		if (this.getMinBin()!=other.getMinBin()) {
			throw new OperationFailedException("Cannot add histograms with different min-bin-values");
		}
		
		for( int i=getMinBin(); i<=getMaxBin(); i++) {
			int otherCnt = other.getCount(i);
			arrIncr(i, otherCnt);
			cnt += otherCnt;
		}
	}
	
	public double mean() throws OperationFailedException {
		
		checkAtLeastOneItemExists();
		
		long sum = 0;
		
		for( int i=minBinVal; i<=maxBinVal; i++) {
			sum += arrGetLong(i)*i;
			assert(sum>=0);
		}
		
		return ((double) sum) / cnt;
	}
	
	public double meanGreaterEqualTo( int val ) throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		long sum = 0;
		long c = 0;
		

		int startMin = Math.max(val, minBinVal);
				
		for( int i=startMin; i<=maxBinVal; i++) {
			long num = arrGetLong(i);
			sum += i*num;
			c += num;
			assert(sum>=0);
			assert(c>=0);
		}
		
		return ((double) sum)/c;
	}
	
	public double meanNonZero() throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		long sum = 0;
		
		for( int i=minBinVal; i<=maxBinVal; i++) {
			sum += i*arrGetLong(i);
			
		}
		
		return ((double) sum)/(cnt-getCount(0));
	}
	
	
	public long sumNonZero() {
		return calcSum() - getCount(0);
	}
	
	public void scaleBy( double factor ) {
		
		int sum = 0;
		
		for( int i=minBinVal; i<=maxBinVal; i++) {
			
			int indx = index(i);
			
			int valNew = (int) Math.round(factor*arr[indx]);
			sum+= valNew;
			arr[indx] = valNew;
		}
		
		cnt = sum;
	}
	
	@Override
	public int quantile( double quantile ) throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		double pos = quantile * cnt;
		
		long sum = 0;
		for( int i=minBinVal; i<=maxBinVal; i++) {
			sum += arrGet(i);
			
			if (sum>pos) {
				return i;
			}
		}
		return calcMax();
	}
	
	@Override
	public int quantileAboveZero( double quantile ) throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		long cntMinusZero = cnt - getCount(0);
		
		double pos = quantile * cntMinusZero;
		
		int startMin = Math.max(1, minBinVal);
		
		long sum = 0;
		for( int i=startMin; i<=maxBinVal; i++) {
			sum += arrGet(i);
			
			if (sum>pos) {
				return i;
			}
		}
		return calcMax();
	}
	
	public boolean hasAboveZero() {

		int startMin = Math.max(1, minBinVal);
		
		for( int i=startMin; i<=maxBinVal; i++) {
			if (arrGet(i)>0) {
				return true;
			}
		}
		return false;
	}
	
	public double percentGreaterEqualTo( int intensity ) {
		
		int startMin = Math.max(intensity, minBinVal);
		
		long sum = 0;
		for( int i=startMin; i<=maxBinVal; i++) {
			sum += arrGet(i);
		}
		
		return ((double) sum) / cnt;
	}
	
	@Override
	public int calcMode() throws OperationFailedException {
		checkAtLeastOneItemExists();
		return calcMode(0);
	}
	
	public int calcMode( int startVal ) throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		int maxIndex = -1;
		int maxValue = -1;
		
		for( int i=startVal; i<=maxBinVal; i++) {
			int val = arrGet(i);
			if (val>maxValue) {
				maxValue = val;
				maxIndex = i;
			}
		}

		return maxIndex;
	}
	

	public int calcMax() throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		for( int i=maxBinVal; i>=minBinVal ;i--) {
			if (arrGet(i)>0) {
				return i;
			}
		}
		
		assert false;
		return -1;
	}

	public int calcMin() throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		for (int i=minBinVal; i<=maxBinVal; i++) {
			if (arrGet(i)>0) {
				return i;
			}			
		}
		
		assert false;
		return -1;
	}

	@Override
	public long calcSum() {
		return calcSumHelper( i->i );
	}
	
	@Override
	public long calcSumSquares() {
		return calcSumHelper( i-> i*i );
	}
	
	@Override
	public long calcSumCubes() {
		return calcSumHelper( i-> i*i*i );
	}
		
	public int calcNumNonZero() {
		
		int num = 0;
		
		for (int i=minBinVal; i<=maxBinVal; i++) {
			if (arrGet(i)>0) {
				num++; 
			}
		}
		
		return num;
	}
	
	@Override
	public double stdDev() throws OperationFailedException {
		checkAtLeastOneItemExists();
		return Math.sqrt( variance() );
	}
	
	@Override
	public long countThreshold(RelationToThreshold relationToThreshold) {

		RelationToValue relation = relationToThreshold.relation();
		double threshold = relationToThreshold.threshold();
		
		long sum = 0;
		
		for (int i=minBinVal; i<=maxBinVal; i++) {
			
			if (relation.isRelationToValueTrue(i, threshold)) {
				sum += arrGetLong(i);
				assert(sum>=0);
			}
		}
		
		return sum;
	}
	
	// The value split becomes part of the higher histogram
	public HistogramsAfterSplit splitAt( int split ) {
		HistogramsAfterSplit out = new HistogramsAfterSplit();
		
		Histogram hLower = threshold(
			new RelationToConstant(
				new LessThanBean(),
				split
			)
		);
		Histogram hHigher = threshold(
			new RelationToConstant(
				new GreaterThanBean(),
				(double) split-1
			)
		);
		
		out.add( hLower );
		out.add( hHigher );
		return out;
	}
	
	// Thresholds (generates a new histogram, existing object is unchanged)
	@Override
	public Histogram threshold(RelationToThreshold relationToThreshold) {

		RelationToValue relation = relationToThreshold.relation();
		double threshold = relationToThreshold.threshold();
		
		HistogramArray out = new HistogramArray(maxBinVal);
		out.cnt = 0;
		for (int i=minBinVal; i<=maxBinVal; i++) {
			
			if (relation.isRelationToValueTrue(i, threshold)) {
				int s = arrGet(i);
				out.arrSet(i, s);
				out.cnt += s;
			}
		}
		
		return out;
	}
	
	// Doesn't show zero values
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int t=minBinVal; t<=maxBinVal; t++) {
			int cnt = arrGet(t);
			if (cnt!=0) {
				sb.append( String.format("%d: %d%n", t, cnt) );
			}
		}
		return sb.toString();
	}
	
	// Includes zero values
	@Override
	public String csvString() {
		StringBuilder sb = new StringBuilder();
		for (int t=minBinVal; t<=maxBinVal; t++) {
			sb.append( String.format("%d, %d%n", t, arrGet(t)) );
		}
		return sb.toString();
	}

	public int getMaxBin() {
		return maxBinVal;
	}
	
	public int getMinBin() {
		return minBinVal;
	}

	@Override
	public long getTotalCount() {
		return cnt;
	}
	
	public Histogram extractPixelsFromRight( long numPixels ) {
		
		Histogram out = new HistogramArray(getMaxBin());
		
		long remaining = numPixels;
		
		// We keep taking pixels from the histogram until we have reached our quota
		for( int b=getMaxBin(); b>=getMinBin(); b-- ) {
			
			int bVal = getCount(b);
			
			// Skip if there's nothing there
			if (bVal==0) {
				continue;
			}
			
			// If there's more or just enough remaining than we have, we transfer the entire bin
			if( remaining >= bVal) {
				out.incrValBy(b, bVal);
				remaining = remaining - bVal;
				
				if (remaining==0) {
					break;
				}
				
			} else {
				out.incrValBy(b, remaining );
				break;
			}
			
		}
		return out;
	}
	
	
	
	public Histogram extractPixelsFromLeft( long numPixels ) {
		
		Histogram out = new HistogramArray(getMaxBin());
		
		long remaining = numPixels;
		
		// We keep taking pixels from the histogram until we have reached our quota
		for( int b=getMinBin(); b<=getMaxBin(); b++ ) {
			
			int bVal = getCount(b);
			
			// Skip if there's nothing there
			if (bVal==0) {
				continue;
			}
			
			// If there's more or just enough remaining than we have, we transfer the entire bin
			if( remaining >= bVal) {
				out.incrValBy(b, bVal);
				remaining = remaining - bVal;
				
				if (remaining==0) {
					break;
				}
				
			} else {
				out.incrValBy(b, toIntExact(remaining) );
				break;
			}
			
		}
		return out;
	}
	
	@Override
	public double mean(double power) throws OperationFailedException {
		checkAtLeastOneItemExists();
		return mean(power, 0.0);
	}

	@Override
	public double mean(double power, double subtractVal) throws OperationFailedException {
		checkAtLeastOneItemExists();
		
		double sum = 0;
		
		for( int i=minBinVal; i<=maxBinVal; i++) {
			double iSub = (i-subtractVal);
			sum += arrGetLong(i) * Math.pow( iSub , power);
		}
		
		return sum / cnt;
	}
	
	private long calcSumHelper( Function<Long,Long> funcInteger ) {
		
		long sum = 0;
		
		for (int i=minBinVal; i<=maxBinVal; i++) {
			long add = arrGetLong(i) * funcInteger.apply( (long) i);
			assert(add>=0);
			sum += add;
			assert(sum>=0);
		}
		
		return sum;
	}

	// The index in the array the value is stored at
	private int index( int val ) {
		return val - minBinVal;
	}
	
	// Sets a count for a value
	private void arrSet( int val, int cntToSet ) {
		arr[ index(val) ] = cntToSet;
	}
	
	// Sets a count for a value
	private void arrIncr( int val, int incrCnt ) {
		arr[ index(val) ] += incrCnt;
	}
	
	private int arrGet( int val ) {
		return arr[ index(val) ];
	}
	
	private long arrGetLong( int val ) {
		return (long) arrGet(val);
	}
		
	private void chopArrBefore( int indx ) {
		arr = Arrays.copyOfRange(arr, indx, arr.length);
	}
	
	private void checkAtLeastOneItemExists() throws OperationFailedException {
		if (isEmpty()) {
			throw new OperationFailedException("There are no items in the histogram so this operation cannot occur");
		}
	}
}
