/*-
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.feature.calculate.cache;

import java.util.Set;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.FeatureInitialization;
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
 * @param <T> feature-input
 */
public interface FeatureSessionCache<T extends FeatureInput> {

    /**
     * Initializes the cache. Should always be called once before any calculations occur
     *
     * @param initialization initialization for the feature.
     * @param logger logger
     */
    public abstract void init(FeatureInitialization initialization, Logger logger);

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
    FeatureSessionCalculator<T> calculator();

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
