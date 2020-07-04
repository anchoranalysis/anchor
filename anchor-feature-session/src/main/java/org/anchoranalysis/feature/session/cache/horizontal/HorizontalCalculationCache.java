package org.anchoranalysis.feature.session.cache.horizontal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;




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
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * Caches CachedCalculations that occur repeatedly across many features, and store similar child caches.
 * <p>
 * The caches are reset every time {@link #invalidate} is called.
 * 
 * @author Owen Feehan
 * @param <T> feature-input type
 */
public class HorizontalCalculationCache<T extends FeatureInput> implements FeatureSessionCache<T> {
	
	private ResettableCachedCalculator<T> calculator;
	
	@SuppressWarnings("unused")
	private SharedFeatureSet<T> sharedFeatures;
		
	private Map<ChildCacheName, FeatureSessionCache<? extends FeatureInput>> children = new HashMap<>();
	
	HorizontalCalculationCache( SharedFeatureSet<T> sharedFeatures ) {
		super();
		this.sharedFeatures = sharedFeatures;
		this.calculator = new ResettableCachedCalculator<>(sharedFeatures);
	}
	
	// Set up the cache
	@Override
	public void init(FeatureInitParams featureInitParams, LogErrorReporter logger) {
		calculator.init(logger);
	}
	
	@Override
	public void invalidate() {
		calculator.invalidate();

		// Invalidate each of the child caches
		for (FeatureSessionCache<? extends FeatureInput> childCache : children.values()) {
			childCache.invalidate();
		}
	}
	
	@Override
	public void invalidateExcept(Set<ChildCacheName> childCacheNames) {

		calculator.invalidate();

		for (Entry<ChildCacheName,FeatureSessionCache<? extends FeatureInput>> entry : children.entrySet()) {
			if (!childCacheNames.contains(entry.getKey())) {
				entry.getValue().invalidate();
			}
		}
	}

	@Override
	public FeatureSessionCacheCalculator<T> calculator() {
		return calculator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(ChildCacheName childName, Class<? extends FeatureInput> inputType, CacheCreator cacheCreator) {
		// Creates a new child-cache if it doesn't already exist for a particular name
		return (FeatureSessionCache<V>) children.computeIfAbsent(
			childName,
			s -> cacheCreator.create(inputType)
		);
	}


}
