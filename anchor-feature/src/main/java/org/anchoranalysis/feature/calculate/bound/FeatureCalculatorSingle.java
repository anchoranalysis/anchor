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
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculator;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Like a {@link FeatureCalculator} but is permanently associated with a single {@link Feature}.
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public interface FeatureCalculatorSingle<T extends FeatureInput> {

    /**
     * Calculate the results of the feature with a particular input.
     *
     * @param input the input to calculate.
     * @return the results of the calculation.
     * @throws FeatureCalculationException if the feature cannot be successfully calculated.
     */
    double calculate(T input) throws FeatureCalculationException;

    /**
     * Calculates the result for an {@code input} recording the error to an {@link ErrorReporter} if
     * anything goes wrong, but throwing no exception.
     *
     * @param input the input to calculate.
     * @param errorReporter where errors are recorded.
     * @return the result of the calculation.
     */
    double calculateSuppressErrors(T input, ErrorReporter errorReporter);
}
