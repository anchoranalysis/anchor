package org.anchoranalysis.feature.session.cache;

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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.cachedcalculation.ResettableSet;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * Caches CachedCalculations that occur repeatedly across many features
 * 
 * The caches are reset, every time reset() is called
 * 
 * @author Owen Feehan
 *
 */
public class HorizontalCalculationCache extends FeatureSessionCache {
	
	private ResettableSet<CachedCalculation<?>> listCC = new ResettableSet<>(false);
	private ResettableSet<CachedCalculationMap<?,?>> listCCMap = new ResettableSet<>(false);
	
	private Retriever retriever = new Retriever();
	
	private LogErrorReporter logger;
	private boolean logCacheInit;
	private SharedFeatureSet sharedFeatures;
	
	HorizontalCalculationCache( SharedFeatureSet sharedFeatures ) {
		super();
		this.sharedFeatures = sharedFeatures;
	}
	
	private class Retriever extends FeatureSessionCacheRetriever {
		
		@Override
		public double calc(Feature feature, CacheableParams<? extends FeatureCalcParams> params )
				throws FeatureCalcException {
			//System.out.printf("Calculating feature: %s\n", feature.getFriendlyName());
	
			double val = feature.calcCheckInit(params);
			if (Double.isNaN(val)) {
				logger.getLogReporter().logFormatted("WARNING: NaN returned from feature %s", feature.getFriendlyName() );
			}
			return val;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> CachedCalculation<T> search(CachedCalculation<T> cc) {
			
			LogErrorReporter loggerToPass = logCacheInit ? logger : null;
			return (CachedCalculation<T>) listCC.findOrAdd(cc,loggerToPass);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <S, T> CachedCalculationMap<S, T> search(
				CachedCalculationMap<S, T> cc) {
			LogErrorReporter loggerToPass = logCacheInit ? logger : null;
			return (CachedCalculationMap<S,T>) listCCMap.findOrAdd(cc,loggerToPass);
		}

		@Override
		public double calcFeatureByID(String id, CacheableParams<? extends FeatureCalcParams> params)
				throws FeatureCalcException {
			try {
				Feature feature = sharedFeatures.getException(id);
				return calc( feature, params );
			} catch (NamedProviderGetException e) {
				throw new FeatureCalcException(
					String.format("Cannot locate feature with resolved-ID: %s", id ),
					e.summarize()
				);
			}
		}

		@Override
		public SharedFeatureSet getSharedFeatureList() {
			return sharedFeatures;
		}

		@Override
		public String resolveFeatureID(String id) {
			return id;
		}

		@Override
		public String describeCaches() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("listCC:\n");
			sb.append(listCC.describe());
			sb.append("\n");
			
			sb.append("listCCMap:\n");
			sb.append(listCCMap.describe());
			sb.append("\n");
			
			return sb.toString();
		}

		@Override
		public FeatureSessionCache createNewCache() {
			return HorizontalCalculationCache.this.duplicate();
		}

		@Override
		public boolean hasBeenInit() {
			return hasBeenInit;
		}
	}
	
	private boolean hasBeenInit = false;
	
	// Set up the cache
	@Override
	public void init(FeatureInitParams featureInitParams, LogErrorReporter logger, boolean logCacheInit) throws InitException {
		this.logger = logger;
		this.logCacheInit = logCacheInit;
		this.hasBeenInit = true;
		assert(logger!=null);

	}
	
	

	@Override
	public void invalidate() {
		listCC.reset();
		listCCMap.reset();
	}

	@Override
	public FeatureSessionCacheRetriever retriever() {
		return retriever;
	}

	@Override
	public void assignResult(FeatureSessionCache other) throws OperationFailedException {
		
		HorizontalCalculationCache otherCast = (HorizontalCalculationCache) other;
		listCC.assignResult( otherCast.listCC );
		listCCMap.assignResult( otherCast.listCCMap );
	}



	@Override
	public FeatureSessionCache duplicate() {
		HorizontalCalculationCache out = new HorizontalCalculationCache( sharedFeatures.duplicate() );
		assert(hasBeenInit==true);
		assert(logger!=null);
		
		out.logger = logger;
		out.logCacheInit = logCacheInit;
		out.listCC = listCC.duplicate();
		out.listCCMap = listCCMap.duplicate();
		out.hasBeenInit = hasBeenInit;
	
		return out;
	}
}
