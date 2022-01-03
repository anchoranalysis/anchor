/*-
 * #%L
 * anchor-feature-session
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.feature.session.calculator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorSingle;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Adds a cache to a {@link FeatureCalculatorSingle} or {@link FeatureCalculatorMulti}.
 *
 * <p>This caches the results created by the calculators. It is not an internal cache used within
 * the feature-calculation itself.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureCalculatorCache {

    private static final int DEFAULT_CACHE_SIZE = 1000;

    /**
     * Creates a cached version of a {@link FeatureCalculatorSingle} with a default cache-size.
     *
     * @param <T> feature-input type
     * @param calculator the calculator to cache.
     * @return a calculator that caches {@link FeatureCalculatorSingle}.
     */
    public static <T extends FeatureInput> FeatureCalculatorSingle<T> cache(
            FeatureCalculatorSingle<T> calculator) {
        return new CachedSingle<>(calculator, DEFAULT_CACHE_SIZE);
    }

    /**
     * Creates a cached version of a {@link FeatureCalculatorSingle} with a particular cache-size.
     *
     * @param <T> feature-input type
     * @param calculator the calculator to cache.
     * @param cacheSize how many items are retained maximally in the cache.
     * @return a calculator that caches {@link FeatureCalculatorSingle}.
     */
    public static <T extends FeatureInput> FeatureCalculatorSingle<T> cache(
            FeatureCalculatorSingle<T> calculator, int cacheSize) {
        return new CachedSingle<>(calculator, cacheSize);
    }

    /**
     * Creates a cached version of a {@link FeatureCalculatorMulti} with a default cache-size.
     *
     * @param <T> feature-input type
     * @param calculator the calculator to cache.
     * @return a calculator that caches {@link FeatureCalculatorMulti}.
     */
    public static <T extends FeatureInput> FeatureCalculatorMulti<T> cache(
            FeatureCalculatorMulti<T> calculator) {
        return new CachedMulti<>(calculator, DEFAULT_CACHE_SIZE);
    }

    /**
     * Creates a cached version of a {@link FeatureCalculatorMulti} with a particular cache-size.
     *
     * @param <T> feature-input type
     * @param calculator the calculator to cache.
     * @param cacheSize how many items are retained maximally in the cache.
     * @return a calculator that caches {@link FeatureCalculatorMulti}.
     */
    public static <T extends FeatureInput> FeatureCalculatorMulti<T> cache(
            FeatureCalculatorMulti<T> calculator, int cacheSize) {
        return new CachedMulti<>(calculator, cacheSize);
    }
}
