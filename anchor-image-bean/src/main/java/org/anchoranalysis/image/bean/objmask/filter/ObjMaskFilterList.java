package org.anchoranalysis.image.bean.objmask.filter;

/*
 * #%L
 * anchor-image-bean
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

import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.bean.annotation.BeanField;

public class ObjMaskFilterList extends NullParamsBean<ObjMaskFilterList> implements Collection<ObjMaskFilter>, Iterable<ObjMaskFilter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1564079429359322186L;
	
	// START BEAN PROPERTIES
	@BeanField
	private List<ObjMaskFilter> list = new ArrayList<>();
	// END BEAN PROPERTIES
	
	public void add(int arg0, ObjMaskFilter arg1) {
		list.add(arg0, arg1);
	}

	@Override
	public boolean add(ObjMaskFilter arg0) {
		return list.add(arg0);
	}

	@Override
	public boolean addAll(Collection<? extends ObjMaskFilter> arg0) {
		return list.addAll(arg0);
	}

	public boolean addAll(int arg0, Collection<? extends ObjMaskFilter> arg1) {
		return list.addAll(arg0, arg1);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return list.containsAll(arg0);
	}

	@Override
	public boolean equals(Object arg0) {
		return list.equals(arg0);
	}

	public ObjMaskFilter get(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	public int indexOf(Object arg0) {
		return list.indexOf(arg0);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<ObjMaskFilter> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object arg0) {
		return list.lastIndexOf(arg0);
	}

	public ListIterator<ObjMaskFilter> listIterator() {
		return list.listIterator();
	}

	public ListIterator<ObjMaskFilter> listIterator(int arg0) {
		return list.listIterator(arg0);
	}

	public ObjMaskFilter remove(int arg0) {
		return list.remove(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		return list.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return list.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return list.retainAll(arg0);
	}

	public ObjMaskFilter set(int arg0, ObjMaskFilter arg1) {
		return list.set(arg0, arg1);
	}

	@Override
	public int size() {
		return list.size();
	}

	public List<ObjMaskFilter> subList(int arg0, int arg1) {
		return list.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return list.toArray(arg0);
	}

	@Override
	public String toString() {
		return list.toString();
	}

	public List<ObjMaskFilter> getList() {
		return list;
	}

	public void setList(List<ObjMaskFilter> list) {
		this.list = list;
	}
}
