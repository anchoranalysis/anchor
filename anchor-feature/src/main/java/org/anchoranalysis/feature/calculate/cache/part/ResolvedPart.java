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

import lombok.AllArgsConstructor;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.part.CalculationPart;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Like a {@link CalculationPart} but has been resolved against a cache to reuse any existing
 * identical instance.
 *
 * @author Owen Feehan
 * @param <S> result-type of the calculation
 * @param <T> feature input-type
 */
@AllArgsConstructor
public class ResolvedPart<S, T extends FeatureInput> {

    /** The cacheable-calculation that is now considered resolved */
    private CalculationPart<S, T> calculation;

    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from previously.
     *
     * @param input If there is no existing cached-value, and the calculation occurs, these
     *     parameters are used. Otherwise ignored.
     * @return the result of the calculation
     * @throws FeatureCalculationException if the calculation cannot finish, for whatever reason
     */
    public S getOrCalculate(T input) throws FeatureCalculationException {
        return calculation.getOrCalculate(input);
    }

    // We delegate to the CachedCalculation to check equality. Needed for the search.
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResolvedPart) {
            return ((ResolvedPart<S, T>) obj).calculation.equals(calculation);
        } else {
            return false;
        }
    }

    // We delegate to the CachedCalculation to check hashCode. Needed for the search.
    @Override
    public int hashCode() {
        return calculation.hashCode();
    }
}
