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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.core.error.OperationFailedException;

public class HistogramsAfterSplit {
	
	private List<Histogram> list = new ArrayList<>();

	public boolean add(Histogram e) {
		return list.add(e);
	}

	public Histogram get(int index) {
		return list.get(index);
	}
	
	

	public static class IndexMean implements Comparable<IndexMean> {
		private int index;
		private double mean;
		private double variance;
		
		public IndexMean(int index, double mean, double variance) {
			super();
			this.index = index;
			this.mean = mean;
			this.variance = variance;
		}

		@Override
		public int compareTo(IndexMean o) {
			return Double.compare(mean, o.mean);
		}

		public int getIndex() {
			return index;
		}

		public double getMean() {
			return mean;
		}


		public double getVariance() {
			return variance;
		}
		
		
	}
	
	public Histogram mergeFromZeroUntil( int t, List<IndexMean> sortedIndexes ) throws OperationFailedException {
		
		if (t==0) {
			return list.get( sortedIndexes.get(0).getIndex() );
		}
		
		Histogram h = list.get( sortedIndexes.get(0).getIndex() ).duplicate();
		
		for( int i=1; i<=t; i++ ) {
			h.addHistogram( list.get(sortedIndexes.get(i).getIndex()) );
		}
		
		return h;
	}
	
	
	public Histogram mergeFromIndexUntilEnd( int t, List<IndexMean> sortedIndexes ) throws OperationFailedException {
		
		if (t==(list.size()-1)) {
			return list.get( sortedIndexes.get(list.size()-1).getIndex() );
		}
		
		Histogram h = list.get( sortedIndexes.get(t).getIndex() ).duplicate();
		
		for( int i=(t+1); i<list.size(); i++ ) {
			h.addHistogram( list.get(sortedIndexes.get(i).getIndex()) );
		}
		
		return h;
	}
		
	public List<IndexMean> indicesSortedByMeanAscending() throws OperationFailedException {
		ArrayList<IndexMean> out = new ArrayList<>();
		
		int i = 0;
		for( Histogram h : list ) {
			out.add(
				new IndexMean(
					i++,
					h.mean(),
					h.variance()
				)
			);
		}
		
		Collections.sort(out);
		
		return out;
	}
	
	public void removeEmpty() {
		
		Iterator<Histogram> itr = list.iterator();
		while( itr.hasNext() ) {
			
			Histogram h = itr.next();
			
			if (h.getTotalCount()==0) {
				itr.remove();
			}
		}
	}

	public int size() {
		return list.size();
	}
	
}