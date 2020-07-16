/* (C)2020 */
package org.anchoranalysis.feature.session.cache.horizontal;

import java.util.ArrayList;
import java.util.Collection;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class HorizontalFeatureCacheFactory implements FeatureSessionCacheFactory {

    private FeatureSessionCacheFactory delegate;
    private Collection<String> ignorePrefixes;

    public HorizontalFeatureCacheFactory() {
        super();
        this.delegate = new HorizontalCalculationCacheFactory();
        this.ignorePrefixes = new ArrayList<>();
    }

    @Override
    public <T extends FeatureInput> FeatureSessionCache<T> create(
            FeatureList<T> namedFeatures, SharedFeatureSet<T> sharedFeatures) {

        FeatureSessionCache<T> cacheCalculation = delegate.create(namedFeatures, sharedFeatures);

        return new HorizontalFeatureCache<>(
                cacheCalculation, namedFeatures, sharedFeatures, ignorePrefixes);
    }
}
