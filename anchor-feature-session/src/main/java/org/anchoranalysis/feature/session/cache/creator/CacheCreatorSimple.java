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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.creator.CacheCreator;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.CachePlus;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class CacheCreatorSimple implements CacheCreator {

	private FeatureList<? extends FeatureCalcParams> namedFeatures;
	private SharedFeatureSet<? extends FeatureCalcParams> sharedFeatures;
	private FeatureInitParams featureInitParams;
	private LogErrorReporter logger;
	
	public CacheCreatorSimple(FeatureList<? extends FeatureCalcParams> namedFeatures,
			SharedFeatureSet<? extends  FeatureCalcParams> sharedFeatures, FeatureInitParams featureInitParams, LogErrorReporter logger) {
		super();
		this.namedFeatures = namedFeatures;
		this.sharedFeatures = sharedFeatures;
		this.featureInitParams = featureInitParams;
		this.logger = logger;
	}

	@Override
	public <T extends FeatureCalcParams> FeatureSessionCache<T> create( Class<?> paramsType ) {
		
		FeatureList<T> featureList = filterFeatureList(paramsType);
		SharedFeatureSet<T> sharedFeaturesCast = maybeCastSharedFeatures(paramsType); 
				
		try {
			CachePlus<T> cache = new CachePlus<>(
				featureList,
				sharedFeaturesCast,
				featureInitParams,
				logger
			);
			return cache.createCache();
		} catch (InitException e) {
			logger.getErrorReporter().recordError(CacheCreatorSimple.class, e);
			assert(false);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private <T extends FeatureCalcParams> FeatureList<T> filterFeatureList(Class<?> paramsType) {
		
		List<Feature<T>> list = namedFeatures.getList().stream()
			.filter( f -> paramsType.isAssignableFrom(f.getClass()) )
			.map( f -> (Feature<T>) f )
			.collect( Collectors.toList() );
		
		return new FeatureList<>(list);
	}
	
	// Assumes either all features in the set match paramsType or none do
	@SuppressWarnings("unchecked")
	private <T extends FeatureCalcParams> SharedFeatureSet<T> maybeCastSharedFeatures(Class<?> paramsType) {
		
		if (sharedFeatures.keys().isEmpty()) {
			return new SharedFeatureSet<T>();
		}
		
		Class<?> classOfArbitraryItem = sharedFeatures.arbitraryItem().getClass(); 
		
		// Take an arbitrary item
		if(paramsType.isAssignableFrom(classOfArbitraryItem)) {
			return (SharedFeatureSet<T>) sharedFeatures;
		}
		
		return new SharedFeatureSet<T>();	
	}
}
