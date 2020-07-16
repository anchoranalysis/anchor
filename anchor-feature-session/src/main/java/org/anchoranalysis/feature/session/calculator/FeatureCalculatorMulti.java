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

package org.anchoranalysis.feature.session.calculator;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Calculates one or more features for given params
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public interface FeatureCalculatorMulti<T extends FeatureInput> {

    /** Performs one calculation throwing an exception if something goes wrong */
    ResultsVector calc(T input) throws FeatureCalcException;

    /**
     * Performs one calculation on a sub-set of the feature list throwing an exception if something
     * goes wrong
     */
    ResultsVector calc(T input, FeatureList<T> featuresSubset) throws FeatureCalcException;

    /**
     * Performs one calculation recording the error to an ErrorReporter if anything goes wrong, but
     * throwing no exception
     */
    ResultsVector calcSuppressErrors(T input, ErrorReporter errorReporter);

    /**
     * Performs one calculation, either calling {@link #calc(T)} or {@link #calcSuppressErrors}
     * depending on a flag
     *
     * @throws FeatureCalcException if suppress errors is FALSE and an error occurs during
     *     calculation
     */
    default ResultsVector calc(T input, ErrorReporter errorReporter, boolean suppressErrors)
            throws FeatureCalcException {
        if (suppressErrors) {
            return calcSuppressErrors(input, errorReporter);
        } else {
            return calc(input);
        }
    }

    /**
     * The number of features that is calculated on each call to calc(), and therefore the size of
     * the ResultsVector returned
     */
    int sizeFeatures();
}
