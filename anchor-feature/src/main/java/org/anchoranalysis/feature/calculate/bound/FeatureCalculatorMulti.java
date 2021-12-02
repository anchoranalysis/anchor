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

package org.anchoranalysis.feature.calculate.bound;

import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculator;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * Like a {@link FeatureCalculator} but is permanently associated with <i>one or more</i> {@link
 * Feature}s.
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public interface FeatureCalculatorMulti<T extends FeatureInput> {

    /**
     * Calculate the results of the features with a particular input.
     *
     * @param input the input to calculate.
     * @return the results of the calculation.
     * @throws NamedFeatureCalculateException if any feature cannot be successfully calculated.
     */
    ResultsVector calculate(T input) throws NamedFeatureCalculateException;

    /**
     * Calculates the results of a subset of the features with a particular input.
     *
     * @param input the input to calculate.
     * @param featuresSubset the subset of features (from those bound to the current instance) to
     *     calculate with.
     * @return the results of the calculation.
     * @throws NamedFeatureCalculateException if any feature cannot be successfully calculated.
     */
    ResultsVector calculate(T input, FeatureList<T> featuresSubset)
            throws NamedFeatureCalculateException;

    /**
     * Calculates the results for an {@code input} recording the error to an {@link ErrorReporter}
     * if anything goes wrong, but throwing no exception.
     *
     * @param input the input to calculate.
     * @param errorReporter where errors are recorded.
     * @return the results of the calculation.
     */
    ResultsVector calculateSuppressErrors(T input, ErrorReporter errorReporter);

    /**
     * Calculates the results for an {@code input}, either calling {@link #calculate} or {@link
     * #calculateSuppressErrors} depending on a flag.
     *
     * @param input the input to calculate.
     * @param errorReporter where errors are recorded.
     * @param suppressErrors if true, errors are recorded via the {@code errorReporter}. if false,
     *     they are thrown as exceptions.
     * @return the results of the calculation.
     * @throws NamedFeatureCalculateException if {@code suppressErrors==false} and an error occurs
     *     during calculation.
     */
    default ResultsVector calculate(T input, ErrorReporter errorReporter, boolean suppressErrors)
            throws NamedFeatureCalculateException {
        if (suppressErrors) {
            return calculateSuppressErrors(input, errorReporter);
        } else {
            return calculate(input);
        }
    }

    /**
     * The number of features that is calculated on each call to {@link #calculate}, and therefore
     * the size of the returned {@link ResultsVector}.
     *
     * @return the number of features.
     */
    int sizeFeatures();
}
