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

package org.anchoranalysis.feature.calculate.part;

import java.util.HashMap;
import java.util.Map;
import org.anchoranalysis.feature.calculate.cache.ResettableCalculation;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Similar to a {@link CalculationPart} but stores several evaluation results, differentiated by a
 * key.
 *
 * @author Owen Feehan
 * @param <S> result-type
 * @param <T> feature input-type
 * @param <U> key-type
 * @param <E> an exception thrown if something goes wrong during the calculation
 */
public abstract class CalculationPartMap<S, T extends FeatureInput, U, E extends Exception>
        implements ResettableCalculation {

    /** Caches results for different keys. */
    private Map<U, S> cache;

    /**
     * Creates for a particular cache-size.
     *
     * @param cacheSize cache-size to use for the keys.
     */
    protected CalculationPartMap(int cacheSize) {
        cache = new HashMap<>();
    }

    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from a previous execution.
     *
     * @param input used to calculate a result, if there is no value already cached. Ignored if a
     *     result already exists.
     * @param key the key, which determines if a result already exists or not.
     * @return the result of the calculation.
     * @throws E if the calculation cannot finish, for whatever reason.
     */
    public S getOrCalculate(T input, U key) throws E {

        S obj = cache.get(key);
        if (obj == null) {
            obj = execute(input, key);
            put(key, obj);
        }
        return obj;
    }

    /**
     * Number of items currently stored in cache.
     *
     * @return then number of items.
     */
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
     * Gets an existing result for the current input from the cache.
     *
     * @param key the key that identifies the existing result.
     * @return a cached-result, or null if it doesn't exist.
     */
    protected S getOrNull(U key) {
        return cache.get(key);
    }

    /**
     * Does a result exist for the current input, and a particular key?
     *
     * @param key the key.
     * @return true iff a result exists.
     */
    protected boolean hasKey(U key) {
        return cache.get(key) != null;
    }

    /**
     * Assigns a evaluation result to the cache for a particular key.
     *
     * @param key the key.
     * @param result the result to assign.
     */
    protected void put(U key, S result) {
        cache.put(key, result);
    }

    /**
     * Calculates a result for a particular input and key.
     *
     * @param input the input.
     * @param key the key.
     * @return the evaluated result.
     * @throws E if something goes wrong during calculation.
     */
    protected abstract S execute(T input, U key) throws E;
}
