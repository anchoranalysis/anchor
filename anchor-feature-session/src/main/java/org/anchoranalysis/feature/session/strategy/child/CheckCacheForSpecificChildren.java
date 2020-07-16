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
/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.child;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;

/**
 * For particular child-caches, check if a session-input is available from another LRU-cache and
 * reuse.
 *
 * <p>This is used to "redirect" certain child-caches to reuse sessions elsewhere.
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public class CheckCacheForSpecificChildren implements FindChildStrategy {

    private Class<?> cacheInputType;
    private CacheTransferSourceCollection source;

    // The fallback cache is used if none of the existing child caches contain the item, or have
    //  been initialized yet. It's created lazily.
    private CacheAndReuseStrategy<FeatureInput> fallbackCache = null;

    public CheckCacheForSpecificChildren(
            Class<?> cacheInputType, CacheTransferSourceCollection source) {
        super();
        this.source = source;
        this.cacheInputType = cacheInputType;
    }

    @Override
    public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
            FeatureSessionCache<?> parentCache,
            CacheCreator factory,
            ChildCacheName childName,
            V input)
            throws FeatureCalcException {

        if (cacheInputType.isAssignableFrom(input.getClass()) && source.contains(childName)) {
            return useSessionFromSource(childName, input, factory);
        }

        return parentCache.childCacheFor(childName, input.getClass(), factory);
    }

    private <V extends FeatureInput> FeatureSessionCache<V> useSessionFromSource(
            ChildCacheName childName, V input, CacheCreator factory) throws FeatureCalcException {
        for (CacheTransferSource<?> src : source) {

            if (src.containsChild(childName)) {

                try {
                    // Check cache if has a session input to match, and if so, reuse it
                    @SuppressWarnings("unchecked")
                    Optional<SessionInput<V>> opt =
                            ((CacheTransferSource<V>) src).getInputIfPresent(input);
                    if (opt.isPresent()) {
                        return opt.get().getCache();
                    }
                } catch (OperationFailedException e) {
                    throw new FeatureCalcException(e);
                }
            }
        }

        // If we haven't found a session-input in any of our existing caches, then we create a new
        // session-input of our own
        return useFallbackCache(input, factory);
    }

    @SuppressWarnings("unchecked")
    private <V extends FeatureInput> FeatureSessionCache<V> useFallbackCache(
            V input, CacheCreator factory) throws FeatureCalcException {
        if (fallbackCache == null) {
            fallbackCache = new CacheAndReuseStrategy<>(factory);
        }
        return (FeatureSessionCache<V>) fallbackCache.createOrReuse(input).getCache();
    }

    @Override
    public FindChildStrategy strategyForGrandchild() {
        return DefaultFindChildStrategy.instance;
    }

    @Override
    public Optional<Set<ChildCacheName>> cachesToAvoidInvalidating() {
        return Optional.of(source.getAllCacheNames());
    }
}
