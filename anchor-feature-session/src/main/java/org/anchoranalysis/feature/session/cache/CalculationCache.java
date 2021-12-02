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

package org.anchoranalysis.feature.session.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calculate.FeatureCalculator;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationCache;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesSubset;

/**
 * Caches calculations that occur repeatedly across many features, and store similar child caches.
 *
 * <p>The caches are reset every time {@link #invalidate} is called.
 *
 * @author Owen Feehan
 * @param <T> feature-input type
 */
class CalculationCache<T extends FeatureInput> implements FeatureCalculationCache<T> {

    private ResettableCalculator<T> calculator;

    @SuppressWarnings("unused")
    private SharedFeaturesSubset<T> sharedFeatures;

    private Map<ChildCacheName, FeatureCalculationCache<? extends FeatureInput>> children =
            new HashMap<>();

    public CalculationCache(SharedFeaturesSubset<T> sharedFeatures) {
        this.sharedFeatures = sharedFeatures;
        this.calculator = new ResettableCalculator<>(sharedFeatures);
    }

    // Set up the cache
    @Override
    public void initialize(FeatureInitialization initialization, Logger logger) {
        calculator.initialize(logger);
    }

    @Override
    public void invalidate() {
        calculator.invalidate();

        // Invalidate each of the child caches
        for (FeatureCalculationCache<? extends FeatureInput> childCache : children.values()) {
            childCache.invalidate();
        }
    }

    @Override
    public void invalidateExcept(Set<ChildCacheName> childCacheNames) {

        calculator.invalidate();

        for (Entry<ChildCacheName, FeatureCalculationCache<? extends FeatureInput>> entry :
                children.entrySet()) {
            if (!childCacheNames.contains(entry.getKey())) {
                entry.getValue().invalidate();
            }
        }
    }

    @Override
    public FeatureCalculator<T> calculator() {
        return calculator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends FeatureInput> FeatureCalculationCache<V> childCacheFor(
            ChildCacheName childName,
            Class<? extends FeatureInput> inputType,
            CacheCreator cacheCreator) {
        // Creates a new child-cache if it doesn't already exist for a particular name
        return (FeatureCalculationCache<V>)
                children.computeIfAbsent(childName, s -> cacheCreator.create(inputType));
    }
}
