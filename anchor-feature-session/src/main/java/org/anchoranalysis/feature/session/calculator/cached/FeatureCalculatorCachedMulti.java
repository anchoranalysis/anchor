package org.anchoranalysis.feature.session.calculator.cached;

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


import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.cache.LRUCache.CacheRetrievalFailed;
import org.anchoranalysis.core.cache.LRUCache.CalculateForCache;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

/**
 * A {@link #FeatureCalculatorMulti} but calculations are cached to avoid repetition if equal {@link FeatureInput} are passed.
 * 
 * @author Owen Feehan
 *
 */
public class FeatureCalculatorCachedMulti<T extends FeatureInput> implements FeatureCalculatorMulti<T> {

	private FeatureCalculatorMulti<T> source;
	
	private LRUCache<T,ResultsVector> cacheResults;

	private static int DEFAULT_CACHE_SIZE = 1000;
	
	private boolean suppressErrors = false;
	
	// We update this every time so it matches whatever is passed to calcSuppressErrors
	private ErrorReporter errorReporter = null;
	

	/**
	 * Creates a feature-calculator with a new cache
	 * 
	 * @param source the underlying feature-calculator to use for calculating unknown results
	 * @param suppressErrors
	 */
	public FeatureCalculatorCachedMulti(FeatureCalculatorMulti<T> source, boolean suppressErrors) {
		this(source, suppressErrors, DEFAULT_CACHE_SIZE);
	}
	
	/**
	 * Creates a feature-calculator with a new cache
	 * 
	 * @param source the underlying feature-calculator to use for calculating unknown results
	 * @param suppressErrors
	 * @param cacheSize size of cache to use
	 */
	public FeatureCalculatorCachedMulti(FeatureCalculatorMulti<T> source, boolean suppressErrors, int cacheSize) {
		super();
		this.source = source;
		this.suppressErrors = suppressErrors;
		
		this.cacheResults = new LRUCache<T,ResultsVector>(
			cacheSize,
			new GetterImpl()
		);
		
	}

	@Override
	public ResultsVector calcSuppressErrors(T input, ErrorReporter errorReporter) {
		
		this.errorReporter = errorReporter;
		try {
			return cacheResults.get(input);
		} catch (GetOperationFailedException e) {
			errorReporter.recordError(FeatureCalculatorCachedMulti.class, e.getCause());
			return createNaNVector(e);
		}
	}

	@Override
	public ResultsVector calc(T input) throws FeatureCalcException {
		try {
			return cacheResults.get(input);
		} catch (GetOperationFailedException e) {
			throw new FeatureCalcException(e.getCause());
		}
	}
	

	@Override
	public ResultsVector calc(T input, FeatureList<T> featuresSubset) throws FeatureCalcException {
		// TODO how should we cache?
		throw new FeatureCalcException("This operation is not supported");
	}


	@Override
	public int sizeFeatures() {
		return source.sizeFeatures();
	}
	
	/** Checks whether the cache contains a particular input already. Useful for tests. */
	public boolean has(T key) {
		return cacheResults.has(key);
	}
	
	/** Number of items currently in the cache */
	public long sizeCurrentLoad() {
		return cacheResults.sizeCurrentLoad();
	}
	
	private class GetterImpl implements CalculateForCache<T,ResultsVector> {

		@Override
		public ResultsVector calculate(T index)	throws CacheRetrievalFailed {
			try {
				if (suppressErrors) {
					return source.calcSuppressErrors(index,errorReporter);
				} else {
					return source.calc(index);
				}
			} catch (FeatureCalcException e) {
				throw new CacheRetrievalFailed(e);
			}
		}
		
	}
	
	/** Return a vector with all NaNs */
	private ResultsVector createNaNVector(GetOperationFailedException e) {
		ResultsVector rv = new ResultsVector( source.sizeFeatures() );
		rv.setErrorAll(e);
		return rv;
	}
}
