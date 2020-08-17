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
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Remembers the first-calculation and always returns this value for all subsequent calculations
 *
 * @author Owen Feehan
 */
public class FeatureCalculatorMultiReuse<T extends FeatureInput>
        implements FeatureCalculatorMulti<T> {

    private FeatureCalculatorMulti<T> delegate;

    private ResultsVector rv = null;

    public FeatureCalculatorMultiReuse(FeatureCalculatorMulti<T> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public ResultsVector calculateSuppressErrors(T params, ErrorReporter errorReporter) {
        if (rv == null) {
            rv = delegate.calculateSuppressErrors(params, errorReporter);
        }
        return rv;
    }

    @Override
    public ResultsVector calculate(T params) throws NamedFeatureCalculateException {
        if (rv == null) {
            rv = delegate.calculate(params);
        }
        return rv;
    }

    @Override
    public ResultsVector calculate(T params, FeatureList<T> featuresSubset)
            throws NamedFeatureCalculateException {
        throw new NamedFeatureCalculateException("This operation is not supported");
    }

    @Override
    public int sizeFeatures() {
        return delegate.sizeFeatures();
    }
}
