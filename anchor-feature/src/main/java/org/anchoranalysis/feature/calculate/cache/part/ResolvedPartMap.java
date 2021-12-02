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

package org.anchoranalysis.feature.calculate.cache.part;

import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.part.CalculationPartMap;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A {@link CalculationPartMap} that has been resolved against a cache.
 *
 * @author Owen Feehan
 * @param <S> result-type
 * @param <T> feature input-type
 * @param <U> key-type
 */
public class ResolvedPartMap<S, T extends FeatureInput, U> {

    private CalculationPartMap<S, T, U, FeatureCalculationException> map;

    /**
     * Creates with a map.
     *
     * @param map the {@link CalculationPartMap} that is now considered resolved.
     */
    public ResolvedPartMap(CalculationPartMap<S, T, U, FeatureCalculationException> map) {
        this.map = map;
    }

    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from a previous execution.
     *
     * @param input used to calculate a result, if there is no value already cached. Ignored if a
     *     result already exists.
     * @param key the key, which determines if a result already exists or not.
     * @return the result of the calculation.
     * @throws FeatureCalculationException if the calculation cannot successfully complete.
     */
    public S getOrCalculate(T input, U key) throws FeatureCalculationException {
        return map.getOrCalculate(input, key);
    }

    // We delegate to the map to check equality. Needed for the search.
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResolvedPartMap) {
            return ((ResolvedPartMap<S, T, U>) obj).map.equals(map);
        } else {
            return false;
        }
    }

    // We delegate to the map to check the hash-code. Needed for the search.
    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
