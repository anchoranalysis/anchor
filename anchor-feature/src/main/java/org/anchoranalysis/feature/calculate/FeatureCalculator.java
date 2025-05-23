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

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.cache.FeatureSymbolCalculator;
import org.anchoranalysis.feature.calculate.part.CalculationPartResolver;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * Calculates {@link FeatureCalculationInput} when passed a particular {@link Feature} and
 * corresponding {@link FeatureCalculationInput}.
 *
 * @author Owen Feehan
 * @param <T> feature-input type
 */
public interface FeatureCalculator<T extends FeatureInput>
        extends CalculationPartResolver<T>, FeatureSymbolCalculator<T> {

    /**
     * Calculate the result of feature with a particular input.
     *
     * @param feature the feature to calculate.
     * @param input the input to calculate.
     * @return the result of the calculation.
     * @throws FeatureCalculationException if the feature cannot be successfully calculated.
     */
    double calculate(Feature<T> feature, FeatureCalculationInput<T> input)
            throws FeatureCalculationException;

    /**
     * Calculates results for a list of features with a particular input, throwing an exception if a
     * calculation fails.
     *
     * @param features list of features.
     * @param input the input to calculate.
     * @return the results of the calculation for each respective feature, with {@link Double#NaN}
     *     (and the stored exception) if an error occurs.
     * @throws NamedFeatureCalculateException if any feature cannot be successfully calculated.
     */
    default ResultsVector calculate(FeatureList<T> features, FeatureCalculationInput<T> input)
            throws NamedFeatureCalculateException {
        ResultsVector out = new ResultsVector(features.size());
        for (int index = 0; index < features.size(); index++) {

            Feature<T> feature = features.get(index);

            try {
                double val = calculate(feature, input);
                out.set(index, val);
            } catch (FeatureCalculationException e) {
                throw new NamedFeatureCalculateException(feature.getFriendlyName(), e.getMessage());
            }
        }
        return out;
    }
}
