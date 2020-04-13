package org.anchoranalysis.feature.session.cache;

/*-
 * #%L
 * anchor-feature-session
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

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * 
 * @author owen
 *
 * @param <T> feature-calc-params
 */
class HorizontalFeatureCacheRetriever<T extends FeatureCalcParams> extends FeatureSessionCacheRetriever<T> {

	private FeatureSessionCacheRetriever<T> delegate;
	private FeatureResultMap<T> map;
	private Collection<String> ignorePrefixes;
	private CreateCache<FeatureSessionCache<T>> cacheProducer;
			
	public HorizontalFeatureCacheRetriever(
		FeatureSessionCacheRetriever<T> delegate,
		FeatureResultMap<T> map,
		Collection<String> ignorePrefixes,
		CreateCache<FeatureSessionCache<T>> cacheProducer
	) {
		super();
		this.delegate = delegate;
		this.map = map;
		this.ignorePrefixes = ignorePrefixes;
		this.cacheProducer = cacheProducer;
	}

	private Double calcAndAdd(
		Feature<T> feature,
		CacheableParams<T> params
	) throws FeatureCalcException {
		Double result = delegate.calc(feature, params);
		map.add(feature, resolveNameFeature(feature), result);
		return result;
	}
	
	private String resolveNameFeature( Feature<T> feature ) {
		String id = feature.getCustomName();
		if (id!=null && !id.isEmpty()) {
			return resolveFeatureID(id);
		} else {
			return id;
		}
	}
	
	@Override
	public double calc(Feature<T> feature, CacheableParams<T> params) throws FeatureCalcException {
		
		// if there's no custom name, then we don't consider caching
		if (feature.getCustomName()==null || feature.getCustomName().isEmpty()) {
			return delegate.calc(feature, params);
		}
		
		// Otherwise we save the result, and cache it for next time
		Double result = map.getResultFor(feature);
		if (result==null) {
			result = calcAndAdd( feature, params );
		}
		return result;
	}

	@Override
	public <U> CachedCalculation<U> search(CachedCalculation<U> cc) {
		return delegate.search(cc);
	}

	@Override
	public <S, U> CachedCalculationMap<S, U> search(
			CachedCalculationMap<S, U> cc) {
		return delegate.search(cc);
	}

	@Override
	public SharedFeatureSet<T> getSharedFeatureList() {
		return delegate.getSharedFeatureList();
	}
	
	@Override
	public String resolveFeatureID(String id) {
		
		// If any of the prefixes exist, they are removed
		for( String prefix : ignorePrefixes) {
			if (id.startsWith(prefix)) {
				String idPrefixRemoved = id.substring(prefix.length());
				return delegate.resolveFeatureID( idPrefixRemoved );
			}
		}
		return delegate.resolveFeatureID( id );
	}
			
	@Override
	public double calcFeatureByID(String id, CacheableParams<T> params)	throws FeatureCalcException {
		
		// Let's first check if it's in our cache
		Double res = map.getResultFor(id);
		
		if (res!=null) {
			return res;
		}
		
		// If it's not there, then let's find the feature we need to calculate from our list
		Feature<T> feat = map.getFeatureFor(id);
		
		if(feat!=null) {
			return calcAndAdd(feat, params);
		} else {
			// We cannot find our feature throw an error, try the delegate
			return delegate.calcFeatureByID(id, params);
		}
	}


	@Override
	public String describeCaches() {
		return delegate.describeCaches();
	}

	@Override
	public FeatureSessionCache<T> createNewCache() {
		return cacheProducer.create();
	}


	@Override
	public boolean hasBeenInit() {
		return delegate.hasBeenInit();
	}
	
}
