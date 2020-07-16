/* (C)2020 */
package org.anchoranalysis.feature.session.cache.horizontal;

import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class HorizontalCalculationCacheFactory implements FeatureSessionCacheFactory {

    @Override
    public <T extends FeatureInput> FeatureSessionCache<T> create(
            FeatureList<T> namedFeatures, SharedFeatureSet<T> sharedFeatures) {
        return new HorizontalCalculationCache<>(sharedFeatures);
    }
}
