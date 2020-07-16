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
 * Like a {@link CacheableCalculation} but has been resolved against a cache to ensure its unique
 * (singular).
 *
 * <p>This operation should always occur before a cached-calculation is used
 *
 * @author Owen Feehan
 * @param <S> result-type of the calculation
 * @param <T> feature input-type
 */
public class ResolvedCalculation<S, T extends FeatureInput> {

    private CacheableCalculation<S, T, FeatureCalcException> calc;

    /**
     * Constructor
     *
     * @param calc the cacheable-calculation that is now considered resolved
     */
    public ResolvedCalculation(CacheableCalculation<S, T, FeatureCalcException> calc) {
        super();
        this.calc = calc;
    }

    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from previously.
     *
     * @param input If there is no existing cached-value, and the calculation occurs, these
     *     parameters are used. Otherwise ignored.
     * @return the result of the calculation
     * @throws FeatureCalcException if the calculation cannot finish, for whatever reason
     */
    public S getOrCalculate(T input) throws FeatureCalcException {
        return calc.getOrCalculate(input);
    }

    // We delegate to the CachedCalculation to check equality. Needed for the search.
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResolvedCalculation) {
            return ((ResolvedCalculation<S, T>) obj).calc.equals(calc);
        } else {
            return false;
        }
    }

    // We delegate to the CachedCalculation to check hashCode. Needed for the search.
    @Override
    public int hashCode() {
        return calc.hashCode();
    }
}
