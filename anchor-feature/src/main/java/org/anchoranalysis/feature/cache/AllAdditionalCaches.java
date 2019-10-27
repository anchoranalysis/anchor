package org.anchoranalysis.feature.cache;

/*-
 * #%L
 * anchor-feature
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;

public class AllAdditionalCaches {
	
	private Map<String, FeatureSessionCache> map = new HashMap<String,FeatureSessionCache>();
	
	// Deep copy
	public AllAdditionalCaches duplicate() throws CreateException {
		
		AllAdditionalCaches out = new AllAdditionalCaches();
		for( String s : map.keySet() ) {
			out.map.put( new String(s), map.get(s).duplicate() );
		}
		return out;
	}
	

	/**
	 * Creates an array of Caches after retrieving specific keys from the a map
	 * 
	 * @param keys
	 * @param allCaches
	 * @return
	 * @throws InitException
	 */
	public FeatureSessionCacheRetriever[] createRetrievers(	List<String> keys ) throws InitException {
		
		FeatureSessionCacheRetriever[] out = new FeatureSessionCacheRetriever[keys.size()];
				
		for( int i=0; i<keys.size(); i++ ) {
			
			String name = keys.get(i);
			
			FeatureSessionCache cache = map.get(name);
			
			if (cache==null) {
				throw new InitException(
					String.format("Cache '%s' is missing from allCaches", name)	
				);
			}
			
			out[i] = cache.retriever();
		}
		
		return out;
	}
	
	public void invalidate() {

		for( String key : map.keySet() ) {
			FeatureSessionCache add = map.get(key);
			add.invalidate();
		}
	}
	
	public int size() {
		return map.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( Entry<String,FeatureSessionCache> e : map.entrySet() ) {
			sb.append( String.format("add(%s)=%d\n", e.getKey(), System.identityHashCode(e.getValue())) );
		}
		return sb.toString();
	}
	
	public void assignResult(AllAdditionalCaches other) throws OperationFailedException {
		
		for( String keyOther : other.map.keySet() ) {
			FeatureSessionCache cacheOther = other.map.get(keyOther);
						
			FeatureSessionCache cache = map.get(keyOther);
			if (cache==null) {
				throw new OperationFailedException(
					String.format("Missing cache key '%s'",keyOther)
				);
			}
			
			cache.assignResult( cacheOther );
		}
	}

	public FeatureSessionCache get(String arg0) {
		return map.get(arg0);
	}

	public FeatureSessionCache put(String arg0, FeatureSessionCache arg1) {
		return map.put(arg0, arg1);
	}

	public Collection<FeatureSessionCache> values() {
		return map.values();
	}
}
