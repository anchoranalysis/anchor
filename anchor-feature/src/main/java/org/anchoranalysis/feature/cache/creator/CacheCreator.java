package org.anchoranalysis.feature.cache.creator;

import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;

public interface CacheCreator {

	<T extends FeatureCalcParams> FeatureSessionCacheRetriever<T> create( Class<?> paramsType );
}
