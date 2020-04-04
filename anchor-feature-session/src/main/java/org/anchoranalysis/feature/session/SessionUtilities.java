package org.anchoranalysis.feature.session;

import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.session.cache.HorizontalCalculationCacheFactory;

public class SessionUtilities {

	private static FeatureSessionCacheFactory FACTORY = new HorizontalCalculationCacheFactory();
	
	public static CacheableParams<FeatureCalcParams> createCacheable(FeatureCalcParams params) {
		return new CacheableParams<>(params, FACTORY);
	}
	
	public static CacheableParams<FeatureInitParams> createCacheableInit(FeatureInitParams params) {
		return new CacheableParams<>(params, FACTORY);
	}
}
