package org.anchoranalysis.anchor.mpp.feature.mark;

/*
 * #%L
 * anchor-mpp
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
import java.util.List;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MemoList implements MemoForIndex {

	private List<PxlMarkMemo> delegate = new ArrayList<>();
	
	@Override
	public boolean equals(Object obj) {
		
		if (super.equals(obj)) {
			return true;
		}
		
		MemoList objCast = (MemoList) obj;
		
		if (objCast==null) {
			return false;
		}
		
		if (size()!=objCast.size()) {
			return false;
		}
		
		for (int i=0; i<size(); i++) {
			if (!get(i).equals(objCast.get(i))) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public int hashCode() {

		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append( size() );
		for (int i=0; i<size(); i++) {
			builder.append( get(i).hashCode() );
		}
		return builder.hashCode();
	}	
	
	public boolean hasCacheIDs() {
		
		for(PxlMarkMemo pmm : delegate) {
			if (!pmm.getMark().hasCacheID()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void addAll( MemoForIndex src ) {
		
		for (int i=0; i<src.size(); i++) {
			add( src.getMemoForIndex(i) );
		}
	}
	
	@Override
	public PxlMarkMemo getMemoForIndex(int index) {
		return get(index);
	}

	public int size() {
		return delegate.size();
	}

	public PxlMarkMemo get(int index) {
		return delegate.get(index);
	}

	public boolean add(PxlMarkMemo e) {
		return delegate.add(e);
	}

	public PxlMarkMemo remove(int index) {
		return delegate.remove(index);
	}

	public boolean remove(PxlMarkMemo o) {
		return delegate.remove(o);
	}
}
