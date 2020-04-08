package org.anchoranalysis.feature.session;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.cache.creator.CacheCreator;
import org.anchoranalysis.feature.cache.creator.CacheCreatorSimple;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.session.cache.HorizontalCalculationCacheFactory;
import org.anchoranalysis.feature.session.cache.HorizontalFeatureCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class SessionUtilities {
	
	public static <T extends FeatureCalcParams> CacheableParams<T> createCacheable(
		T params,
		FeatureList<T> featureList,
		SharedFeatureSet<T> sharedFeatures,
		FeatureInitParams featureInitParams,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return createCacheable(
			params,
			new CacheCreatorSimple(featureList, sharedFeatures, featureInitParams, logger)
		);
	}
	
	public static <T extends FeatureCalcParams> List<CacheableParams<T>> createCacheable(
		List<T> list,
		FeatureList<T> featureList,
		SharedFeatureSet<T> sharedFeatures,
		FeatureInitParams featureInitParams,
		LogErrorReporter logger
	) throws FeatureCalcException {
		List<CacheableParams<T>> out = new ArrayList<>();

		for(T params : list) {
			out.add(
				createCacheable(params, featureList, sharedFeatures, featureInitParams, logger)
			);
		}
		
		return out;
	}
	
	
	public static <T extends FeatureCalcParams> CacheableParams<T> createCacheable(T params, CacheCreator cacheFactory ) throws FeatureCalcException {
		
		if (params==null) {
			throw new FeatureCalcException("Params are null. Cannot proceed with feature calculation");
		}
		
		return new CacheableParams<T>(
			params,
			cacheFactory
		);
	}
}
