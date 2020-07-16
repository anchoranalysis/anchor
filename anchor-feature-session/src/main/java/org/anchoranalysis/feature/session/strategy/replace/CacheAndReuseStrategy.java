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
package org.anchoranalysis.feature.session.strategy.replace;

import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Reuse (without needing to invalidate) an existing session-input as stored in a least-recently
 * used cache, otherwise create a new one.
 *
 * @author Owen Feehan
 * @param <T> feature-input
 */
public class CacheAndReuseStrategy<T extends FeatureInput> implements ReplaceStrategy<T> {

    private static final int CACHE_SIZE = 200;

    private LRUCache<T, SessionInput<T>> cache;

    public CacheAndReuseStrategy(CacheCreator cacheCreator) {
        ReplaceStrategy<T> delegate = new AlwaysNew<>(cacheCreator);
        cache = new LRUCache<>(CACHE_SIZE, delegate::createOrReuse);
    }

    @Override
    public SessionInput<T> createOrReuse(T input) throws FeatureCalcException {
        try {
            return cache.get(input);
        } catch (GetOperationFailedException e) {
            throw new FeatureCalcException(e);
        }
    }

    public LRUCache<T, SessionInput<T>> getCache() {
        return cache;
    }
}
