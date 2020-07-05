package org.anchoranalysis.io.manifest.deserializer.bundle;

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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Bundle<T extends Serializable> implements Serializable, Iterable<BundleItem<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 508875270985307740L;

	private List<BundleItem<T>> list;
	
	private int capacity;

	public Bundle() {
		super();
		list = new LinkedList<>(); 
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public void add( String index, T object ) {
		this.list.add( new BundleItem<>(index, object));
	}
	
	public int size() {
		return this.list.size();
	}

	@Override
	public Iterator<BundleItem<T>> iterator() {
		return list.iterator();
	}
	
	public Map<Integer,T> createHashMap() {
		HashMap<Integer,T> hashMap = new HashMap<>();
		for( BundleItem<T> item : list ) {
			hashMap.put( Integer.parseInt(item.getIndex()), item.getObject() );
		}
		return hashMap;
	}
}
