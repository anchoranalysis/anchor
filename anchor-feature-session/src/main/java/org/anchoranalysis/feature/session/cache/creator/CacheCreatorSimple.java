package org.anchoranalysis.feature.session.cache.creator;

/*-
 * #%L
 * anchor-feature-session
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.session.cache.horizontal.HorizontalFeatureCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class CacheCreatorSimple implements CacheCreator {

	private FeatureList<? extends FeatureInput> namedFeatures;
	private SharedFeatureMulti<? extends FeatureInput> sharedFeatures;
	private FeatureInitParams featureInitParams;
	private LogErrorReporter logger;
	
	private static FeatureSessionCacheFactory factory = new HorizontalFeatureCacheFactory();
	
	public CacheCreatorSimple(FeatureList<? extends FeatureInput> namedFeatures,
			SharedFeatureMulti<? extends  FeatureInput> sharedFeatures, FeatureInitParams featureInitParams, LogErrorReporter logger) {
		super();
		this.namedFeatures = namedFeatures;
		this.sharedFeatures = sharedFeatures;
		this.featureInitParams = featureInitParams;
		this.logger = logger;
	}

	@Override
	public <T extends FeatureInput> FeatureSessionCache<T> create( Class<?> paramsType ) {
		
		FeatureList<T> featureList = filterFeatureList(paramsType);
				
		try {
			return createCache(
				featureList,
				paramsType,
				featureInitParams,
				logger	
			);
		} catch (CreateException e) {
			logger.getErrorReporter().recordError(CacheCreatorSimple.class, e);
			assert(false);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private <T extends FeatureInput> FeatureList<T> filterFeatureList(Class<?> paramsType) {
		
		List<Feature<T>> list = namedFeatures.getList().stream()
			.filter( f -> f.paramType().isCompatibleWith(paramsType) )
			.map( f -> (Feature<T>) f )
			.collect( Collectors.toList() );
		
		return new FeatureList<>(list);
	}
		
	private <T extends FeatureInput> FeatureSessionCache<T> createCache(
		FeatureList<T> namedFeatures,
		Class<?> paramsType,
		FeatureInitParams featureInitParams,
		LogErrorReporter logger			
	) throws CreateException {
		
		SharedFeatureSet<T> sharedFeaturesSet = sharedFeatures.maybeCastSharedFeatures(paramsType);
		
		try {
			sharedFeaturesSet.initRecursive(featureInitParams, logger);
		} catch (InitException e) {
			throw new CreateException(e);
		}
		
		assert(logger!=null);
		FeatureSessionCache<T> cache = factory.create(namedFeatures, sharedFeaturesSet);
		try {
			cache.init(featureInitParams, logger);
		} catch (InitException e) {
			throw new CreateException(e);
		}
		return cache;
	}
}
