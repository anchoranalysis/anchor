package org.anchoranalysis.feature.session;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.session.cache.HorizontalCalculationCacheFactory;
import org.anchoranalysis.feature.session.cache.HorizontalFeatureCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class SessionUtilities {
	
	public static CacheableParams<FeatureCalcParams> createCacheable(FeatureCalcParams params, FeatureList featureList, SharedFeatureSet sharedFeatures) {
		
		CachePlus cache = new CachePlus(
			new HorizontalFeatureCacheFactory( new HorizontalCalculationCacheFactory() ),
			featureList,
			sharedFeatures
		);
		
		return createCacheable(
			params,
			() -> cache.retriever().createNewCache().retriever()
		);
	}
	
	public static CacheableParams<FeatureCalcParams> createCacheable(FeatureCalcParams params, Supplier<FeatureSessionCacheRetriever> cacheFactory ) {
		return new CacheableParams<>(params, cacheFactory);
	}
	
	public static List<CacheableParams<? extends FeatureCalcParams>> createCacheable(
		List<FeatureCalcParams> list,
		FeatureList featureList,
		SharedFeatureSet sharedFeatures
	) {
		return list.stream().map(
			params -> createCacheable(params, featureList, sharedFeatures)	
		).collect( Collectors.toList() );
	}
}
