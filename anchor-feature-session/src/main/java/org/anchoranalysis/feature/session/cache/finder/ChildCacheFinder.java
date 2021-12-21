/*-
 * #%L
 * anchor-feature-session
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

package org.anchoranalysis.feature.session.cache.finder;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationCache;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Strategy to determine which child-cache (among hierarchy) to employ for a particular feature
 * calculation.
 */
public interface ChildCacheFinder {

    /**
     * Selects a child-cache given a parent and a child-name.
     *
     * <p>This may involve reusing an existing cache, or creating a new one, depending on the
     * strategy.
     *
     * @param <V> type-of-input to be used to calculate on the child-cache
     * @param parentCache the existing parent-cache
     * @param factory how to create new caches
     * @param childCacheName name of child-cache
     * @param input input to be used for calculations on the child-cache
     * @return an existing or newly created child-cache depending on the strategy.
     * @throws FeatureCalculationException if no child-cache exists or can be created for {@code childCacheName}.
     */
    <V extends FeatureInput> FeatureCalculationCache<V> childCacheFor(
            FeatureCalculationCache<?> parentCache,
            CacheCreator factory,
            ChildCacheName childCacheName,
            V input)
            throws FeatureCalculationException;

    /**
     * What finder to use for children-of-children?
     *
     * @return the finder to use.
     */
    ChildCacheFinder finderForGrandchild();

    /**
     * If set, these particular-caches are exceptionally <b>not</b> invalidated during the typical
     * invalidation operation on their parent.
     * 
     * <p>If not-set, there are no exceptions.
     * 
     * @return the names of the caches that are exceptionally <b>not</b> invalidated, if any exist.
     */
    Optional<Set<ChildCacheName>> cachesToAvoidInvalidating();
}
