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

package org.anchoranalysis.feature.calculate;

import java.util.HashMap;
import java.util.Map;
import org.anchoranalysis.feature.calculate.cache.ResettableCalculation;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Similar to a {@link FeatureCalculation} but stores several evaluations parameterised by a key.
 *
 * @author Owen Feehan
 * @param <S> result-type
 * @param <T> feature input-type
 * @param <U> key-type
 * @param <E> an exception thrown if something goes wrong during the calculation
 */
public abstract class FeatureCalculationMap<S, T extends FeatureInput, U, E extends Exception>
        implements ResettableCalculation {

    /** Caches our results for different Keys */
    private Map<U, S> cache;

    /**
     * Creates for a particular cache-size.
     *
     * @param cacheSize cache-size to use for the keys
     */
    public FeatureCalculationMap(int cacheSize) {
        cache = new HashMap<>();
    }

    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from previously.
     *
     * @param input if there is no cached-value, and the calculation occurs, these parameters are
     *     used. Otherwise ignored..
     * @return the result of the calculation
     * @throws E if the calculation cannot finish, for whatever reason
     */
    public S getOrCalculate(T input, U key) throws E {

        S obj = cache.get(key);
        if (obj == null) {
            obj = execute(input, key);
            put(key, obj);
        }
        return obj;
    }

    /** Number of items currently stored in cache */
    public int numberItemsCurrentlyStored() {
        return cache.size();
    }

    /** Invalidates the cache, removing any items already stored. */
    @Override
    public void invalidate() {
        cache.clear();
    }

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    /**
     * Gets an existing result for the current params from the cache.
     *
     * @param key
     * @return a cached-result, or null if it doesn't exist
     */
    protected S getOrNull(U key) {
        return cache.get(key);
    }

    protected boolean hasKey(U key) {
        return cache.get(key) != null;
    }

    protected void put(U index, S item) {
        cache.put(index, item);
    }

    protected abstract S execute(T input, U key) throws E;
}
