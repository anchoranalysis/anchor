package org.anchoranalysis.feature.session;

/*
 * #%L
 * anchor-feature
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

import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.cache.LRUHashMapCache;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * A SequentialSession but we cache the ResultsVectors in case an identical FeatureCalcParams
 *   is passed in the future
 * 
 * @author Owen Feehan
 *
 */
public class SequentialSessionVerticallyCached<T extends FeatureCalcParams> extends FeatureSession implements ISequentialSessionSingleParams<T> {

	private SequentialSession<T> delegate;
	
	private LRUHashMapCache<ResultsVector,T> hashCache;
	private CacheMonitor cacheMonitor = new CacheMonitor();

	private static int CACHE_SIZE = 1000;
	
	private boolean suppressErrors = false;
	
	// We update this every time so it matches whatever is passed to calcSuppressErrors
	private ErrorReporter errorReporter = null;
	
	public SequentialSessionVerticallyCached(FeatureList<T> listFeatures, boolean suppressErrors, Collection<String> ignoreFeaturePrefixes ) {
		super();
		this.delegate = new SequentialSession<>(listFeatures, ignoreFeaturePrefixes);
		this.suppressErrors = suppressErrors;
		
	}
	
	@Override
	public void start(FeatureInitParams featureInitParams, SharedFeatureSet<T> sharedFeatureList, LogErrorReporter logger) throws InitException {
		delegate.start(featureInitParams, sharedFeatureList, logger);
		
		this.hashCache = LRUHashMapCache.createAndMonitor(CACHE_SIZE, new GetterImpl(), cacheMonitor, "SequentialSessionVerticallyCached");
	}
	
	
	private class GetterImpl implements LRUHashMapCache.Getter<ResultsVector,T> {

		@Override
		public ResultsVector get(T index)
				throws GetOperationFailedException {
			try {
				if (suppressErrors) {
					return delegate.calcOneSuppressErrors(index,errorReporter);
				} else {
					return delegate.calcOne(index);
				}
			} catch (FeatureCalcException e) {
				throw new GetOperationFailedException(e);
			}
		}
		
	}

	@Override
	public ResultsVector calcOneSuppressErrors(T params,
			ErrorReporter errorReporter) {
		this.errorReporter = errorReporter;
		try {
			return hashCache.get(params);
		} catch (GetOperationFailedException e) {
			errorReporter.recordError(SequentialSessionVerticallyCached.class, e.getCause());
			
			// Return a vector with all NaNs
			ResultsVector rv = new ResultsVector( delegate.numFeatures() );
			rv.setErrorAll(e);
			return rv;
		}
	}

	@Override
	public ResultsVector calcOne(T params)
			throws FeatureCalcException {
		return delegate.calcOne(params);
	}


}
