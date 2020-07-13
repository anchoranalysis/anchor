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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.anchoranalysis.anchor.mpp.mark.set.UpdatableMarkSet;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.probmap.ProbMap;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

// The pxlMarkMemoList must always match the current state of the underlzing updatable items
public class ListUpdatableMarkSetCollection implements UpdatableMarkSet, List<UpdatableMarkSet> {
	
	private ArrayList<UpdatableMarkSet> delegate = new ArrayList<>();

	@Override
	public boolean add(UpdatableMarkSet e) {
		return delegate.add(e);
	}

	@Override
	public void initUpdatableMarkSet( MemoForIndex marks, NRGStackWithParams stack, Logger logger, SharedFeatureMulti sharedFeatures ) throws InitException {
		for (UpdatableMarkSet item : delegate) {
			item.initUpdatableMarkSet(marks, stack, logger, sharedFeatures);
		}
	}
	
	@Override
	public void add( MemoForIndex marksExisting, VoxelizedMarkMemo newMark ) throws UpdateMarkSetException {
		for (UpdatableMarkSet item : delegate) {
			item.add(marksExisting, newMark);
		}
	}

	
	// Modifies marksExisting
	private void add( MemoList marksExisting, MemoForIndex listToAdd ) throws UpdateMarkSetException {
		for (int i=0; i<listToAdd.size(); i++) {
			VoxelizedMarkMemo item = listToAdd.getMemoForIndex(i);
			add( marksExisting, item );
			marksExisting.add( item );
		}
	}
	
	// Assumes no existing marks
	public void add( MemoForIndex listToAdd ) throws UpdateMarkSetException {
		add( new MemoList(), listToAdd );
	}
	
	public int numProbMap() {
		
		int cnt = 0;
		
		for (UpdatableMarkSet item : delegate) {

			if (item instanceof ProbMap) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public List<ProbMap> listProbMap() {
		ArrayList<ProbMap> list = new ArrayList<>();
		
		for (UpdatableMarkSet item : delegate) {

			if (item instanceof ProbMap) {
				list.add( (ProbMap) item );
			}
		}
		
		return list;
	}
	
	@Override
	public void exchange( MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo oldMark, int indexOldMark, VoxelizedMarkMemo newMark ) throws UpdateMarkSetException {
		for (UpdatableMarkSet item : delegate) {
			item.exchange(pxlMarkMemoList, oldMark, indexOldMark, newMark);
		}
	}
	
	@Override
	public void rmv( MemoForIndex marksExisting, VoxelizedMarkMemo mark ) throws UpdateMarkSetException {
		for (UpdatableMarkSet item : delegate) {
			item.rmv(marksExisting, mark);
		}
	}
	
	@Override
	public void add(int index, UpdatableMarkSet element) {
		delegate.add(index, element);		
	}

	@Override
	public boolean addAll(Collection<? extends UpdatableMarkSet> c) {
		return delegate.addAll(c);
	}

	@Override
	public boolean addAll(int index,
			Collection<? extends UpdatableMarkSet> c) {
		return delegate.addAll(index, c);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	@Override
	public UpdatableMarkSet get(int index) {
		return delegate.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return delegate.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Iterator<UpdatableMarkSet> iterator() {
		return delegate.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	@Override
	public ListIterator<UpdatableMarkSet> listIterator() {
		return delegate.listIterator();
	}

	@Override
	public ListIterator<UpdatableMarkSet> listIterator(int index) {
		return delegate.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	@Override
	public UpdatableMarkSet remove(int index) {
		return delegate.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return delegate.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return delegate.retainAll(c);
	}

	@Override
	public UpdatableMarkSet set(int index,
			UpdatableMarkSet element) {
		return delegate.set(index, element);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public List<UpdatableMarkSet> subList(int fromIndex, int toIndex) {
		return delegate.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return delegate.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return delegate.toArray(a);
	}
}
