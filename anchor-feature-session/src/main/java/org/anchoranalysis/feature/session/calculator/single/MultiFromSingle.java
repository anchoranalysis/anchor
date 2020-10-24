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

package org.anchoranalysis.feature.session.calculator.single;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;

@AllArgsConstructor
class MultiFromSingle<T extends FeatureInput>
        implements FeatureCalculatorMulti<T> {

    private final FeatureCalculatorSingle<T> delegate;

    @Override
    public ResultsVector calculate(T input) throws NamedFeatureCalculateException {
        try {
            return vectorFor(delegate.calculate(input));
        } catch (FeatureCalculationException e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    @Override
    public ResultsVector calculate(T input, FeatureList<T> featuresSubset)
            throws NamedFeatureCalculateException {
        throw new NamedFeatureCalculateException(
                "The calculation on feature-subsets operation is not supported");
    }

    @Override
    public ResultsVector calculateSuppressErrors(T input, ErrorReporter errorReporter) {
        return vectorFor(delegate.calculateSuppressErrors(input, errorReporter));
    }

    @Override
    public int sizeFeatures() {
        return 1;
    }

    private static ResultsVector vectorFor(double result) {
        ResultsVector out = new ResultsVector(1);
        out.set(0, result);
        return out;
    }
}
