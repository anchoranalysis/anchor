package org.anchoranalysis.feature.session.cache;

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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheCreator;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * Caches repeated-calls to the same feature, or references to a feature by an ID-value
 * 
 * Two separate indexes are made to prior feature-calculations:
 *   a mapping from the Feature (by object reference) to the value
 *   a mapping from the customName (if it exists) to the value
 * 
 * @author Owen Feehan
 *
 */
public class HorizontalFeatureCache<T extends FeatureCalcParams> extends FeatureSessionCache<T> {

	private FeatureSessionCache<T> delegate;
	
	private HorizontalFeatureCacheCalculator<T> retriever;
	
	private FeatureResultMap<T> map = new FeatureResultMap<>();
	
	HorizontalFeatureCache(
		FeatureSessionCache<T> delegate,
		FeatureList<T> namedFeatures,
		SharedFeatureSet<T> sharedFeatures,
		Collection<String> ignorePrefixes
	) {
		super();
		this.delegate = delegate;
		
		try {
			for( Feature<T> f : namedFeatures ) {
				map.add(f);
			}
			
			for( INameValue<Feature<T>> f : sharedFeatures ) {
				map.add(f.getValue());
			}
			
			retriever = new HorizontalFeatureCacheCalculator<>(
				delegate.calculator(),
				map,
				ignorePrefixes
			);
			
		} catch (OperationFailedException e) {
			// TODO
			assert(false);
		}
	}

	@Override
	public void init(FeatureInitParams featureInitParams,
			LogErrorReporter logger, boolean logCacheInit) throws InitException {
		delegate.init(featureInitParams, logger, logCacheInit);
	}

	@Override
	public void invalidate() {
		map.clear();
		delegate.invalidate();
	}

	@Override
	public <V extends FeatureCalcParams> FeatureSessionCache<V> childCacheFor(String childName, Class<?> paramsType,
			CacheCreator cacheCreator) {
		return delegate.childCacheFor(childName, paramsType, cacheCreator);
	}
	
	@Override
	public FeatureSessionCacheCalculator<T> calculator() {
		return retriever;
	}

}
