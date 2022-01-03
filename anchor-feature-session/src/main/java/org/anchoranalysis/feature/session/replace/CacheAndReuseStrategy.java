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

package org.anchoranalysis.feature.session.replace;

import lombok.Getter;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Reuse an existing {@link FeatureCalculationInput}, as stored in a least-recently used cache, and
 * without invalidating it.
 *
 * <p>If no existing input is already stored, create a new one.
 *
 * @author Owen Feehan
 * @param <T> feature-input type
 */
public class CacheAndReuseStrategy<T extends FeatureInput> implements ReplaceStrategy<T> {

    private static final int CACHE_SIZE = 200;

    /** The cache mapping a particular input to a corresponding {@link FeatureCalculationInput}. */
    @Getter private LRUCache<T, FeatureCalculationInput<T>> cache;

    /**
     * Create with a particular {@link CacheCreator}.
     *
     * @param cacheCreator the cache-creator.
     */
    public CacheAndReuseStrategy(CacheCreator cacheCreator) {
        ReplaceStrategy<T> delegate = new AlwaysNew<>(cacheCreator);
        cache = new LRUCache<>(CACHE_SIZE, delegate::createOrReuse);
    }

    @Override
    public FeatureCalculationInput<T> createOrReuse(T input) throws OperationFailedException {
        try {
            return cache.get(input);
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
