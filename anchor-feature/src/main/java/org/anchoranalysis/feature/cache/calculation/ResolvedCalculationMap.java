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

package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A {@link CacheableCalculationMap} that has been resolved against a cache.
 *
 * @author Owen Feehan
 * @param <S> result-type
 * @param <T> feature input-type
 * @param <U> key-type
 */
public class ResolvedCalculationMap<S, T extends FeatureInput, U> {

    private CacheableCalculationMap<S, T, U, FeatureCalcException> map;

    /**
     * Constructor
     *
     * @param map the cacheable-calculation map that is now considered resolved
     */
    public ResolvedCalculationMap(CacheableCalculationMap<S, T, U, FeatureCalcException> map) {
        super();
        this.map = map;
    }

    public S getOrCalculate(T params, U key) throws FeatureCalcException {
        return map.getOrCalculate(params, key);
    }

    // We delegate to the CachedCalculationMap to check equality. Needed for the search.
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResolvedCalculationMap) {
            return ((ResolvedCalculationMap<S, T, U>) obj).map.equals(map);
        } else {
            return false;
        }
    }

    // We delegate to the CachedCalculationMap to check hashCode. Needed for the search.
    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
