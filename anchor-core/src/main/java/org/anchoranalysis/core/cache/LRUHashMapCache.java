package org.anchoranalysis.core.cache;

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


import java.util.HashMap;
import java.util.LinkedList;

import org.anchoranalysis.core.index.GetOperationFailedException;

// A cache of accesses to the instant state
// TODO remove asserts and replace with UnitTyests
/**
 * 
 * @author Owen Feehan
 *
 * @param <V> value-type
 * @param <K> key-type
 */
public class LRUHashMapCache<V,K> {

	// An ordered list of recent cache accesses
	private LinkedList<K> accessHistory = new LinkedList<>();
	
	// Indexed access to the hash cache
	private HashMap<K,V> hashMap = new HashMap<>();
	
	private int cacheSize;
	
	private Getter<V,K> sourceGetter;
	
	@FunctionalInterface
	public static interface Getter<V,K> {
		V get( K index ) throws GetOperationFailedException;
	}
	
	public LRUHashMapCache( int cacheSize, Getter<V,K> getter ) {
		this.cacheSize = cacheSize;
		this.sourceGetter = getter;
	}
	
	public synchronized void clear() {
		accessHistory = new LinkedList<>();
		hashMap = new HashMap<>();
	}
	
	private void removeFromAccessHistory( K index ) {
		while(true) {
			int indexOf = accessHistory.indexOf(index);
			
			if (indexOf==-1) {
				break;
			}
			
			accessHistory.remove(indexOf);
		}
	}
	
	private void assertAccessHistoryDoesNotContain(K index) {
		
		if (accessHistory.contains(index)) {
			assert false;
		}
	}
	
	private void assertHashMapContainsNonNull() {
		for (int i=0; i<accessHistory.size(); i++) {
			assert hashMap.get( accessHistory.get(i)) !=null;
		}
	}
	
	@SuppressWarnings("unused")
	private void assertHashContainedInAccessHistory() { // NOSONAR
		for( K ix : hashMap.keySet()) {
			assert accessHistory.contains(ix);
		}
	}
	
	/**
	 * Checks if an object exists in the cache, without making a new one, if it doesn't
	 * 
	 * @param index does this index exist in the cache?
	 * @return true if it exists, false otherwise
	 */
	public synchronized boolean has( K index ) {
		return hashMap.get(index)!=null;
	}
	
	
	
	public synchronized V get( K index ) throws GetOperationFailedException {
		
		V item = hashMap.get(index);
		
		if (item!=null) {
			// If already in cache
			
			// Make sure we are top of the access history
			if (!accessHistory.getFirst().equals(index)) {
		
				removeFromAccessHistory(index);
				accessHistory.addFirst(index);
			}

			// CACHE HIT IS OCCURRING HERE

			assert accessHistory.size()==hashMap.keySet().size();
			assert accessHistory.size()<=cacheSize;
			assert hashMap.keySet().size() <= cacheSize;
			
			return item;
			
		} else {
			assert !has(index);
			item = sourceGetter.get(index);
			put( index, item );
			return item;
		}
	}
	
	
	/**
	 * Puts a new object in the cache. DOES NOT CHECK if there is an existing value.
	 * 
	 * @param index index of new object
	 * @param value value of new object
	 */
	public synchronized void put( K index, V value ) {
		
		assert value!=null;
		assertHashMapContainsNonNull();
		
		// TODO why is this assert being thrown?  Disabled for now
		//assertHashContainedInAccessHistory(); // NOSONAR
		
		assertAccessHistoryDoesNotContain(index);
		
		// If cfgNRG is not already in cache
		if (hashMap.keySet().size()>=cacheSize) {
			K removed = accessHistory.removeLast();
			
			removeFromAccessHistory(removed);
			hashMap.remove( removed );
			
			assert !accessHistory.contains(removed);
			assert hashMap.keySet().size()==(cacheSize-1);
		}
		assertAccessHistoryDoesNotContain(index);
		
		assert value != null;

		assertAccessHistoryDoesNotContain(index);
		
		if (value!=null) {
			accessHistory.addFirst(index);
			hashMap.put(index, value);
		}
		
		// CACHE MISS OCCURS
	
		assert accessHistory.size()==hashMap.keySet().size();
		assert accessHistory.size()<=cacheSize;
		assert hashMap.keySet().size() <= cacheSize;
	}
	
	public int sizeUsed() {
		return hashMap.size();
	}
}
