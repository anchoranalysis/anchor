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
import java.util.Set;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.IFriendlyException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.store.cachedgetter.CachedGetter;
import org.anchoranalysis.core.name.store.cachedgetter.ProfiledCachedGetter;

/**
 * Items are evaluated only when they are first needed. The value is thereafter stored.
 * 
 * @author Owen Feehan
 *
 * @param <T> item-type in store
 */
public class LazyEvaluationStore<T> extends NamedProviderStore<T> {

	private HashMap<String,CachedGetter<T>> map = new HashMap<>();
	
	private LogErrorReporter logErrorReporter;
	private String storeDisplayName;
	
	public LazyEvaluationStore(LogErrorReporter logErrorReporter, String storeDisplayName) {
		super();
		this.logErrorReporter = logErrorReporter;
		this.storeDisplayName = storeDisplayName;
	}

	@Override
	public T getException(String key) throws GetOperationFailedException {
		
		try {
			CachedGetter<T> cachedGetter = map.get(key);
			
			if (cachedGetter==null) {
				throw new GetOperationFailedException( String.format("NamedItem '%s' does not exist in %s",key, storeDisplayName) );
			}
			
			return cachedGetter.doOperation();
		} catch (ExecuteException e) {
			return throwException(key, e.getCause());
		} catch (Exception e) {
			return throwException(key, e);
		}
	}
	
	private static <T> T throwException( String key, Throwable cause ) throws GetOperationFailedException {
		
		String msg = String.format("An error occurred getting '%s'", key); 
		
		if (cause instanceof IFriendlyException) {
			IFriendlyException causeCast = (IFriendlyException) cause;
			throw new GetOperationFailedException(
				msg + ": " + causeCast.friendlyMessageHierarchy()
			);
		} else {
			throw new GetOperationFailedException(
				new OperationFailedException( msg, cause )
			);
		}
	}

	// We only refer to 
	public Set<String> keysEvaluated() {
		HashSet<String> keysUsed = new HashSet<>();
		for( String key : map.keySet() ) {
			if( map.get(key).isDone() ) {
				keysUsed.add(key);
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
	public void add(String name, Operation<T> getter)
			throws OperationFailedException {
		map.put(name, new ProfiledCachedGetter<>(getter,name,storeDisplayName,logErrorReporter) );
		
	}

	@Override
	public T getNull(String key) throws GetOperationFailedException {
		
		
		try {
			CachedGetter<T> cachedGetter = map.get(key);
			
			if (cachedGetter==null) {
				return null;
			}
			
			return cachedGetter.doOperation();
		} catch (ExecuteException e) {
			throw new GetOperationFailedException(e.getCause());
		}
	}



}
