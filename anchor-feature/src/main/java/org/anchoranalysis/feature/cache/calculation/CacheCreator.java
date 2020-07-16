/* (C)2020 */
package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.input.FeatureInput;

public interface CacheCreator {

    <T extends FeatureInput> FeatureSessionCache<T> create(
            Class<? extends FeatureInput> paramsType);
}
