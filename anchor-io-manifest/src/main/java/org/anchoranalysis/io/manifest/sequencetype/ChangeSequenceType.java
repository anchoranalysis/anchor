package org.anchoranalysis.io.manifest.sequencetype;

/*
 * #%L
 * anchor-io
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


import java.util.TreeSet;

import org.anchoranalysis.core.index.container.IOrderProvider;

public class ChangeSequenceType extends SequenceType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4134961949858208220L;

	private TreeSet<Integer> indexSet = new TreeSet<>();
	
	private int minimumIndex = -1;
	
	private int maximumIndex = -1;
	
	
	@Override
	public String getName() {
		return "change";
	}

	// Assumes updates are sequential
	@Override
	public void update( String indexStr ) throws SequenceTypeException {
		
		int index = Integer.parseInt(indexStr);
		
		if (maximumIndex!=-1 && index <= maximumIndex) {
			throw new SequenceTypeException("indexes are not being addded sequentially");
		}
		
		maximumIndex = index;
		indexSet.add(index);
		
		if (minimumIndex==-1) {
			minimumIndex = index;
		}
	}

	@Override
	public int nextIndex(int index) {
		
		Integer next = indexSet.higher(index);
		if (next!=null) {
			return next;
		} else {
			return -1;
		}
	}

	@Override
	public int previousIndex(int index) {

		Integer prev = indexSet.lower(index);
		if (prev!=null) {
			return prev;
		} else {
			return -1;
		}
	}
	
	@Override
	public int previousEqualIndex(int index) {
	
		if (indexSet.contains(index)) {
			return index;
		} else {
			return previousIndex(index);
		}
	}

	@Override
	public int getMinimumIndex() {
		return minimumIndex;
	}

	@Override
	public int getMaximumIndex() {
		return maximumIndex;
	}

	public void setMaximumIndex(int maximumIndex) {
		if (maximumIndex > this.maximumIndex) {
			this.maximumIndex = maximumIndex;
		}
	}
	

	@Override
	public IOrderProvider createOrderProvider() {
		OrderProviderHashMap map = new OrderProviderHashMap();
		map.addIntegerSet(indexSet);
		return map;
	}

	@Override
	public int getNumElements() {
		return indexSet.size();
	}

	@Override
	public String indexStr(int index) {
		return String.valueOf(index);
	}




}
