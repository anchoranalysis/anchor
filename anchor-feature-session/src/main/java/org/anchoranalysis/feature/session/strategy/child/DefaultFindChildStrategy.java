/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.child;

import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * The default strategy for finding children by always directly taking (or creating a child-cache)
 * as necessary.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultFindChildStrategy implements FindChildStrategy {

    public static final FindChildStrategy instance = new DefaultFindChildStrategy();

    @Override
    public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
            FeatureSessionCache<?> parentCache,
            CacheCreator factory,
            ChildCacheName childName,
            V input) {
        return parentCache.childCacheFor(childName, input.getClass(), factory);
    }

    @Override
    public FindChildStrategy strategyForGrandchild() {
        return instance;
    }

    public static FindChildStrategy instance() {
        return instance;
    }

    @Override
    public Optional<Set<ChildCacheName>> cachesToAvoidInvalidating() {
        return Optional.empty();
    }
}
