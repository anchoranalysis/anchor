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
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.cachedgetter.ProfiledCachedGetter;

/**
 * Items are evaluated only when they are first needed. The value is thereafter stored.
 * 
 * @author Owen Feehan
 *
 * @param <T> item-type in the store
 */
public class LazyEvaluationStore<T> implements NamedProviderStore<T> {

	private HashMap<String,	WrapOperationAsCached<T,OperationFailedException>> map = new HashMap<>();
	
	private Logger logger;
	private String storeDisplayName;
	
	public LazyEvaluationStore(Logger logger, String storeDisplayName) {
		super();
		this.logger = logger;
		this.storeDisplayName = storeDisplayName;
	}

	@Override
	public T getException(String key) throws NamedProviderGetException {
		return getOptional(key).orElseThrow( ()->
			NamedProviderGetException.nonExistingItem(key, storeDisplayName)
		);
	}
	
	@Override
	public Optional<T> getOptional(String key) throws NamedProviderGetException {
		try {
			return OptionalUtilities.map(
				Optional.ofNullable( map.get(key) ),
				WrapOperationAsCached::doOperation
			);
		} catch (Exception e) {
			throw NamedProviderGetException.wrap(key, e);
		}
	}

	// We only refer to 
	public Set<String> keysEvaluated() {
		HashSet<String> keysUsed = new HashSet<>();
		for(Entry<String,WrapOperationAsCached<T,OperationFailedException>> entry : map.entrySet()) {
			if( entry.getValue().isDone() ) {
				keysUsed.add(entry.getKey());
			}
		}
		return keysUsed;
	}
	
	// All keys that it is possible to evaluate
	@Override
	public Set<String> keys() {
		return map.keySet();
	}
	
	@Override
	public void add(String name, Operation<T,OperationFailedException> getter) throws OperationFailedException {
		map.put(
			name,
			new ProfiledCachedGetter<>(getter,name,storeDisplayName,logger)
		);
		
	}
}
