package org.anchoranalysis.core.name.store;

/*
 * #%L
 * anchor-bean
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
import java.util.Map;

import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * Objects shared between different components
 * Components can re-use existing objects, or add new objects as they prefer
 * 
 * TODO: we probably will need to implement some form of reference counting later
 *   so we can remove items that are no longer necessary, but for now we don't do this
 * 
 * @author Owen Feehan
 *
 */
public class SharedObjects {
	
	/**
	 * A set of NamedItemStores, partitioned by Class<?>
	 */
	private Map<Class<?>,NamedProviderStore<?>> setStores;
	
	private LogErrorReporter logger;
	
	public SharedObjects(
		LogErrorReporter logger
	) {
		this.logger = logger;
		assert logger!=null;
		
		setStores = new HashMap<>();
	}
	
	/**
	 * Gets an existing store, or creates a new one
	 * 
	 * @param key unique-identifier for the store
	 * @param <T> type of item in store
	 * @param <E> exception thrown by type in store
	 * @return an existing-store, or a newly-created one
	 */
	@SuppressWarnings("unchecked")
	public <T> NamedProviderStore<T> getOrCreate( Class<?> key ) {
		
		NamedProviderStore<?> store = setStores.get(key);
		
		if (store==null) {
			store = new LazyEvaluationStore<>(logger,key.getSimpleName());
			setStores.put(key, store);
		}
		
		return (NamedProviderStore<T>) store;
	}
}

