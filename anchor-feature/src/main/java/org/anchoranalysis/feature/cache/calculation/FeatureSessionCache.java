/* (C)2020 */
package org.anchoranalysis.feature.cache.calculation;

import java.util.Set;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A context in which to calculate features while caching certain duplicated internal calculations
 * among the features.
 *
 * <p>When a user calculates a a single feature, they often are calculating other features at the
 * same time (on one or more objects). This creates possibilities for various different forms of
 * caching of results, and different initialization strategies.
 *
 * <p>This class represents one such bunch of calculations. Different implementations provide
 * different strategies.
 *
 * <p>Each session-cache may contain "child" caches for particular string identifiers. This provides
 * a hierarchy of caches and sub-caches as many features change the underlying objects that are
 * being calculated, and need separate space in the cache.
 *
 * <p>A call to {{@link #invalidate()} removes any existing caching (also in the children) and
 * guarantees the next calculation will be fresh/
 *
 * @author Owen Feehan
 * @param T feature-input
 */
public interface FeatureSessionCache<T extends FeatureInput> {

    /**
     * Initialises the cache. Should always be called once before any calculations occur
     *
     * @param featureInitParams params for initialization
     * @param logger logger
     */
    public abstract void init(FeatureInitParams featureInitParams, Logger logger);

    /** Invalidates existing caches so all calculations occur freshly */
    void invalidate();

    /**
     * Invalidates existing caches so all calculations occur freshly - except some particular
     * child-caches who are not invalidated
     */
    void invalidateExcept(Set<ChildCacheName> childCacheNames);

    /**
     * A means of calculating feature values using this cache.
     *
     * @return
     */
    FeatureSessionCacheCalculator<T> calculator();

    /**
     * Gets/creates a child-cache for a given name
     *
     * <p>This function trusts the caller to use the correct type for the child-cache.
     *
     * @param <V> params-type of the child cache to found
     * @param childName name of the child-cache
     * @param inputType the type of V
     * @param cacheCreator factory for creating a cache
     * @return the existing or new child cache of the given name
     */
    <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
            ChildCacheName childName,
            Class<? extends FeatureInput> inputType,
            CacheCreator cacheCreator);
}
