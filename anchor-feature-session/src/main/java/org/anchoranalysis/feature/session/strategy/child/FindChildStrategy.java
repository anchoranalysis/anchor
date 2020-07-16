/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.child;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public interface FindChildStrategy {

    /**
     * Selects a child-cache given a parent and a child-name
     *
     * <p>This may involve reusing an existing cache, or creating a new one, depending on the
     * strategy..
     *
     * @param <V> type-of-input to be used to calculate on the child-cache
     * @param parentCache the existing parent-cache
     * @param factory how to create new caches
     * @param childCacheName name of child-cache
     * @param input input to be used for calculations on the child-cache
     * @return an existing or newly created child-cache depending on the strategy.
     */
    <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
            FeatureSessionCache<?> parentCache,
            CacheCreator factory,
            ChildCacheName childCacheName,
            V input)
            throws FeatureCalcException;

    /**
     * What strategy to use for children-of-children?
     *
     * @return the strategy
     */
    FindChildStrategy strategyForGrandchild();

    /**
     * If set, these particular-caches are exceptionall NOT invalidated during the typical
     * invalidation operation on their parent. If not-set, there are no exceptions.
     */
    Optional<Set<ChildCacheName>> cachesToAvoidInvalidating();
}
