package org.anchoranalysis.feature.session.cache.creator;

import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;

public interface CacheCreator {

	<T extends FeatureInput> FeatureSessionCache<T> create( Class<?> paramsType );
}
