package org.anchoranalysis.feature.session.cache.horizontal;

import java.util.HashMap;
import java.util.Map;

import org.anchoranalysis.core.error.InitException;

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


import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheableCalculation;
import org.anchoranalysis.feature.cache.calculation.CacheableCalculationMap;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.session.cache.creator.CacheCreator;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * Caches CachedCalculations that occur repeatedly across many features
 * 
 * The caches are reset, every time reset() is called
 * 
 * @author Owen Feehan
 * @param parameter-type
 */
public class HorizontalCalculationCache<T extends FeatureInput> extends FeatureSessionCache<T> {
	
	private ResettableSet<CacheableCalculation<?,T>> setCC = new ResettableSet<>(false);
	private ResettableSet<CacheableCalculationMap<?,T,?>> setCCMap = new ResettableSet<>(false);
	
	private Calculator retriever = new Calculator();
	
	private LogErrorReporter logger;
	private boolean logCacheInit;
	private SharedFeatureSet<T> sharedFeatures;
		
	private Map<String, FeatureSessionCache<FeatureInput>> children = new HashMap<>();
	
	HorizontalCalculationCache( SharedFeatureSet<T> sharedFeatures ) {
		super();
		this.sharedFeatures = sharedFeatures;
	}
	
	private class Calculator extends FeatureSessionCacheCalculator<T> {
		
		@Override
		public double calc(Feature<T> feature, SessionInput<T> input )
				throws FeatureCalcException {
			double val = feature.calcCheckInit(input);
			if (Double.isNaN(val)) {
				logger.getLogReporter().logFormatted("WARNING: NaN returned from feature %s", feature.getFriendlyName() );
			}
			return val;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <U> ResolvedCalculation<U,T> search(CacheableCalculation<U,T> cc) {
			
			LogErrorReporter loggerToPass = logCacheInit ? logger : null;
			return new ResolvedCalculation<>(
				(CacheableCalculation<U,T>) setCC.findOrAdd(cc,loggerToPass)
			);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <S, U> ResolvedCalculationMap<S,T,U> search(CacheableCalculationMap<S,T,U> cc) {
			LogErrorReporter loggerToPass = logCacheInit ? logger : null;
			return new ResolvedCalculationMap<>( 
				(CacheableCalculationMap<S,T,U>) setCCMap.findOrAdd(cc,loggerToPass)
			);
		}

		@Override
		public double calcFeatureByID(String id, SessionInput<T> input)
				throws FeatureCalcException {
			try {
				Feature<T> feature = sharedFeatures.getException(id);
				return calc( feature, input );
			} catch (NamedProviderGetException e) {
				throw new FeatureCalcException(
					String.format("Cannot locate feature with resolved-ID: %s", id ),
					e.summarize()
				);
			}
		}

		@Override
		public String resolveFeatureID(String id) {
			return id;
		}
	}
	
	// Set up the cache
	@Override
	public void init(FeatureInitParams featureInitParams, LogErrorReporter logger, boolean logCacheInit) throws InitException {
		this.logger = logger;
		this.logCacheInit = logCacheInit;
		assert(logger!=null);

	}
	
	@Override
	public void invalidate() {
		
		setCC.invalidate();
		setCCMap.invalidate();

		// Invalidate each of the child caches
		for (FeatureSessionCache<FeatureInput> childCache : children.values()) {
			childCache.invalidate();
		}
	}

	@Override
	public FeatureSessionCacheCalculator<T> calculator() {
		return retriever;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(String childName, Class<?> paramsType, CacheCreator cacheCreator) {
		// Creates a new child-cache if it doesn't already exist for a particular name
		return (FeatureSessionCache<V>) children.computeIfAbsent(
			childName,
			s -> cacheCreator.create(paramsType)
		);
	}
}
