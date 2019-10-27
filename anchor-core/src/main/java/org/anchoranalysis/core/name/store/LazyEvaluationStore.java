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

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.memory.MemoryUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;

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
			throw new GetOperationFailedException( String.format("An error occurred getting '%s'",key), e.getCause());
		} catch (Exception e) {
			throw new GetOperationFailedException( String.format("An error occurred getting '%s'",key), e);
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

	private static class CachedGetter<T> extends CachedOperation<T> {

		private Operation<T> getter;
		
		public CachedGetter(Operation<T> getter) {
			super();
			this.getter = getter;
		}

		@Override
		protected T execute() throws ExecuteException {
			return getter.doOperation(); 
		}

	}
	
	
	//
	//  WE HACK USING STATIC VARIABLES TO MONITOR CALLS TO PROFILE CACHED GETTER FROM DIFFERENT STORES
	//
	// As it is recurisvely called during the evaluation process, we subtract
	//   the time spent in other 'execute' methods from the main one
	private static class ProfiledCachedGetter<T> extends CachedGetter<T> {

		private String name;
		private String storeDisplayName;
		private LogErrorReporter logErrorReporter;

		private static int cnt = 0;
		private static long subExecTime = 0; 
		private static long subMem = 0;
		
		private static final int STORE_DISPLAY_NAME_LENGTH = 30;
		private static final int NAME_LENGTH = 30;
		
		public ProfiledCachedGetter(Operation<T> getter, String name, String storeDisplayName, LogErrorReporter logErrorReporter) {
			super(getter);
			this.logErrorReporter = logErrorReporter;
			
			// We pad the display name to a fixed with
			this.name = StringUtils.rightPad( name, NAME_LENGTH );
			this.storeDisplayName = StringUtils.rightPad( storeDisplayName, STORE_DISPLAY_NAME_LENGTH );
		}
		
		@Override
		protected T execute() throws ExecuteException {
			cnt++;

			StopWatch sw = new StopWatch();
			sw.start();
			
			long memoryBefore = MemoryUtilities.calcMemoryUsage();
		
			T obj = super.execute();
			long timeTaken = Math.max( sw.getTime() - subExecTime, 0 );

			long memoryAfter = MemoryUtilities.calcMemoryUsage();
			long memoryAdded = memoryAfter - memoryBefore;
			
			
			long memoryUsed = Math.max( memoryAdded - subMem, 0);
			
			logErrorReporter.getLogReporter().log( String.format("Execution Time \t(%6dms, %7dkb)\t%s\t%s", timeTaken, memoryUsed/1000, name, storeDisplayName ) );
			cnt--;
			
			if (cnt==0) {
				subExecTime=0;
				subMem = 0;
			} else {
				subExecTime = timeTaken;
				subMem = memoryAdded;
			}
			
			
			
			return obj;
		}
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
