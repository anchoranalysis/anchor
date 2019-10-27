package org.anchoranalysis.core.index.container;

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


import java.util.List;

import org.anchoranalysis.core.index.GetOperationFailedException;

public class BoundedIndexContainerFromList<T> implements IBoundedIndexContainer<T> {

	private List<T> list;
	
	public BoundedIndexContainerFromList( List<T> list ) {
		this.list = list;
	}

	@Override
	public void addBoundChangeListener(BoundChangeListener cl) {
		// NOTHING TO DO
	}

	@Override
	public int nextIndex(int index) {
		if (index<(list.size()-1)) {
			return index+1;
		} else {
			return -1;
		}
	}

	@Override
	public int previousIndex(int index) {
		if (index>0) {
			return index-1; 
		}
		return -1;
	}

	@Override
	public int previousEqualIndex(int index) {
		return index;
	}

	@Override
	public int getMinimumIndex() {
		return 0;
	}

	@Override
	public int getMaximumIndex() {
		return list.size()-1;
	}

	@Override
	public T get(int index) throws GetOperationFailedException {
		return list.get(index);
	}
	
}
