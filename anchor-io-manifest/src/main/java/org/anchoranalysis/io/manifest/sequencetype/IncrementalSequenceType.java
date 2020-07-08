package org.anchoranalysis.io.manifest.sequencetype;

/*-
 * #%L
 * anchor-io-manifest
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.index.container.OrderProvider;

import lombok.Getter;
import lombok.Setter;

public class IncrementalSequenceType extends SequenceType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896806957547954271L;

	private int end = -1;
	
	@Getter @Setter
	private int start = 0;

	@Getter @Setter
	private int incrementSize = 1;
	
	public IncrementalSequenceType() {
		this(0);
	}
	
	public IncrementalSequenceType(int start) {
		super();
		this.start = start;
	}

	@Override
	public String getName() {
		return "incremental";
	}

	@Override
	public void update(String indexStr) throws SequenceTypeException {
	
		int index = Integer.parseInt(indexStr);
		
		// If end has been set, we always increment
		if (end==-1) {
			end = start;
		} else {
			end += incrementSize;
		}

		// If the passed index is not what we expect
		// This is where we temporarily disabled when we create large video
		if (index!=end) {
			throw new SequenceTypeException("Incorrectly ordered index in update");
		}
		
	}
	
	@Override
	public int nextIndex(int indexIn) {
		double numPeriods = indexToNumPeriods(indexIn);
		int indexOut = numPeriodsToIndex( Math.floor(numPeriods+1) );
		if (indexOut <= getMaximumIndex()) {
			return indexOut;
		} else {
			return -1;
		}
	}

	@Override
	public int previousIndex(int indexIn) {
		double numPeriods = indexToNumPeriods(indexIn);
		int indexOut = numPeriodsToIndex( Math.ceil(numPeriods-1) );
		if (indexOut >= getMinimumIndex()) {
			return indexOut;
		} else {
			return -1;
		}
	}


	@Override
	public int previousEqualIndex(int index) {
		
		double numPeriods = indexToNumPeriods(index);
		if ( Math.floor(numPeriods)==numPeriods ) {
			return index;
		} else {
			return previousIndex(index);
		}
	}
	
	@Override
	public int getMinimumIndex() {
		return 0;
	}

	@Override
	public int getMaximumIndex() {
		return end;
	}

	@Override
	public OrderProvider createOrderProvider() {
		return index -> indexToNumPeriodsInt( Integer.valueOf(index) );
	}

	public void setEnd(int end) {
		
		if (start==-1) {
			throw new IllegalArgumentException("Please call setStart before setEnd");
		}
		
		if ( ((end-start)%incrementSize) != 0 ) {
			throw new IllegalArgumentException("End is not incrementally reachable from start");
		}
		this.end = end;
	}

	@Override
	public int getNumElements() {
		return (getRangeIndex() / incrementSize) + 1;
	}
	
	@Override
	public String indexStr(int index) {
		return String.valueOf(index);
	}
	
	private int getRangeIndex() {
		return getMaximumIndex() - getMinimumIndex() + 1;
	}
	
	private int indexToNumPeriodsInt( int index ) {
		int norm = (index - start);
		return norm / incrementSize;
	}
	
	private double indexToNumPeriods( int index ) {
		int norm = (index - start);
		return ((double) norm) / incrementSize;
	}
	
	private int numPeriodsToIndex( double numPeriods ) {
		return (int) ( numPeriods*incrementSize) + start;
	}
}
