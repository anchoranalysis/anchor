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

package org.anchoranalysis.feature.calculate.cache;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * Calculates features in the context of a particular feature-session.
 *
 * @author Owen Feehan
 * @param <T> feature-input type that the cache supports
 */
public interface FeatureSessionCalculator<T extends FeatureInput>
        extends CalculationResolver<T>, FeatureSymbolCalculator<T> {

    /**
     * Calculate a feature with particular values
     *
     * @param feature feature
     * @param input feature-input
     * @return the feature-value
     * @throws FeatureCalculationException
     */
    double calculate(Feature<T> feature, SessionInput<T> input) throws FeatureCalculationException;

    /**
     * Calculates a feature-list throwing an exception if there is an error
     *
     * @param features list of features
     * @param input params
     * @return the results of each feature, with Double.NaN (and the stored exception) if an error
     *     occurs
     */
    default ResultsVector calc(FeatureList<T> features, SessionInput<T> input)
            throws NamedFeatureCalculateException {
        ResultsVector out = new ResultsVector(features.size());
        for (int i = 0; i < features.size(); i++) {

            Feature<T> f = features.get(i);

            try {
                double val = calculate(f, input);
                out.set(i, val);
            } catch (FeatureCalculationException e) {
                throw new NamedFeatureCalculateException(f.getFriendlyName(), e.getMessage());
            }
        }
        return out;
    }
}
