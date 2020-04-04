package org.anchoranalysis.feature.session;

import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;

public class SessionUtilities {

	public static CacheableParams<FeatureCalcParams> createCacheable(FeatureCalcParams params) {
		return new CacheableParams<>(params);
	}
	
	public static CacheableParams<FeatureInitParams> createCacheableInit(FeatureInitParams params) {
		return new CacheableParams<>(params);
	}
}
