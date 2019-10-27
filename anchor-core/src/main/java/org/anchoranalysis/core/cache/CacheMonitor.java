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


import java.util.Collection;

import org.apache.commons.collections.map.MultiValueMap;

// Monitors all active caches
public class CacheMonitor {

	private MultiValueMap map;
	
	public CacheMonitor() {
		map = new MultiValueMap();
	}
	
	public void add( String name, LRUHashMapCache<?,?> item ) {
		map.put(name, item);
	}
	
	public String shortSummary() {
		StringBuilder sb = new StringBuilder();
		
		for (Object keyObj : map.keySet()) {
			
			String key = (String) keyObj;
			
			@SuppressWarnings("unchecked")
			Collection< LRUHashMapCache<?,?>> items = (Collection< LRUHashMapCache<?,?>>) map.get(key);
			
			for( LRUHashMapCache<?,?> item : items ) {
				sb.append( item.sizeUsed() );
				sb.append(" ");
			}
			
		}
		
		return sb.toString();
	}
}
