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

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.calculate.FeatureCalculator;
import org.anchoranalysis.feature.calculate.cache.part.ResolvedPart;
import org.anchoranalysis.feature.calculate.cache.part.ResolvedPartMap;
import org.anchoranalysis.feature.calculate.part.CalculationPart;
import org.anchoranalysis.feature.calculate.part.CalculationPartMap;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesSubset;

@RequiredArgsConstructor
class ResettableCalculator<T extends FeatureInput> implements FeatureCalculator<T> {

    // START REQUIRED ARGUMENTS
    private final SharedFeaturesSubset<T> sharedFeatures;
    // END REQUIRED ARGUMENTS

    private ResettableSet<CalculationPart<?, T>> setCalculation = new ResettableSet<>(false);
    private ResettableSet<CalculationPartMap<?, T, ?, FeatureCalculationException>>
            setCalculationMap = new ResettableSet<>(false);

    /** Invalidates internal caches (resets!) so all items will be calculated anew. */
    public void invalidate() {
        setCalculation.invalidate();
        setCalculationMap.invalidate();
    }

    @Override
    public double calculate(Feature<T> feature, FeatureCalculationInput<T> input)
            throws FeatureCalculationException {
        return feature.calculateCheckInitialized(input);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> ResolvedPart<U, T> search(CalculationPart<U, T> calculation) {
        return new ResolvedPart<>(
                (CalculationPart<U, T>) setCalculation.findOrAdd(calculation, null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, U> ResolvedPartMap<S, T, U> search(
            CalculationPartMap<S, T, U, FeatureCalculationException> calculation) {
        return new ResolvedPartMap<>(
                (CalculationPartMap<S, T, U, FeatureCalculationException>)
                        setCalculationMap.findOrAdd(calculation, null));
    }

    @Override
    public double calculateFeatureByIdentifier(String id, FeatureCalculationInput<T> input)
            throws FeatureCalculationException {
        try {
            Feature<T> feature = sharedFeatures.getException(id);
            return calculate(feature, input);
        } catch (NamedProviderGetException e) {
            throw new FeatureCalculationException(
                    String.format("Cannot locate feature with resolved-ID: %s", id), e.summarize());
        }
    }

    @Override
    public String resolveFeatureIdentifier(String identifier) {
        return identifier;
    }
}
