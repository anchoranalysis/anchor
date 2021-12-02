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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationCache;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.replace.CacheAndReuseStrategy;

/**
 * For particular child-caches, check if a session-input is available from another LRU-cache and
 * reuse.
 *
 * <p>This is used to "redirect" certain child-caches to reuse sessions elsewhere.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class CheckCacheForSpecificChildren implements ChildCacheFinder {

    // START REQUIRED ARGUMENTS
    private final Class<?> cacheInputType;
    private final CacheTransferSourceCollection source;
    // END REQUIRED ARGUMENTS

    // The fallback cache is used if none of the existing child caches contain the item, or have
    //  been initialized yet. It's created lazily.
    private CacheAndReuseStrategy<FeatureInput> fallbackCache = null;

    @Override
    public <V extends FeatureInput> FeatureCalculationCache<V> childCacheFor(
            FeatureCalculationCache<?> parentCache,
            CacheCreator factory,
            ChildCacheName childName,
            V input)
            throws FeatureCalculationException {

        if (cacheInputType.isAssignableFrom(input.getClass()) && source.contains(childName)) {
            return useSessionFromSource(childName, input, factory);
        }

        return parentCache.childCacheFor(childName, input.getClass(), factory);
    }

    private <V extends FeatureInput> FeatureCalculationCache<V> useSessionFromSource(
            ChildCacheName childName, V input, CacheCreator factory)
            throws FeatureCalculationException {

        for (CacheTransferSource<?> src : source) {

            if (src.containsChild(childName)) {

                try {
                    // Check cache if has a session input to match, and if so, reuse it
                    @SuppressWarnings("unchecked")
                    Optional<FeatureCalculationInput<V>> opt =
                            ((CacheTransferSource<V>) src).getInputIfPresent(input);
                    if (opt.isPresent()) {
                        return opt.get().getCache();
                    }
                } catch (OperationFailedException e) {
                    throw new FeatureCalculationException(e);
                }
            }
        }

        // If we haven't found a session-input in any of our existing caches, then we create a new
        // session-input of our own
        try {
            return useFallbackCache(input, factory);
        } catch (CreateException e) {
            throw new FeatureCalculationException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <V extends FeatureInput> FeatureCalculationCache<V> useFallbackCache(
            V input, CacheCreator factory) throws CreateException {
        if (fallbackCache == null) {
            fallbackCache = new CacheAndReuseStrategy<>(factory);
        }
        return (FeatureCalculationCache<V>) fallbackCache.createOrReuse(input).getCache();
    }

    @Override
    public ChildCacheFinder finderForGrandchild() {
        return DefaultChildCacheFinder.instance();
    }

    @Override
    public Optional<Set<ChildCacheName>> cachesToAvoidInvalidating() {
        return Optional.of(source.getAllCacheNames());
    }
}
