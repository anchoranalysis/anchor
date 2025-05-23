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

import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Exposes a {@link FeatureCalculatorMulti} as a {@link FeatureCalculatorSingle}.
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public class FeatureCalculatorSingleFromMulti<T extends FeatureInput>
        implements FeatureCalculatorSingle<T> {

    private FeatureCalculatorMulti<T> delegate;

    /**
     * Creates from a {@link FeatureCalculatorMulti}.
     *
     * @param multi the calculator to expose as a {@link FeatureCalculatorSingle}.
     * @throws InitializeException if {@code multi} has more than one feature.
     */
    public FeatureCalculatorSingleFromMulti(FeatureCalculatorMulti<T> multi)
            throws InitializeException {
        this.delegate = multi;
        if (delegate.sizeFeatures() != 1) {
            throw new InitializeException(
                    String.format(
                            "When creating a %s, the multi must have exactly one feature",
                            FeatureCalculatorSingle.class.getSimpleName()));
        }
    }

    @Override
    public double calculateSuppressErrors(T input, ErrorReporter errorReporter) {
        return delegate.calculateSuppressErrors(input, errorReporter).get(0);
    }

    @Override
    public double calculate(T input) throws FeatureCalculationException {
        try {
            return delegate.calculate(input).get(0);
        } catch (NamedFeatureCalculateException e) {
            throw e.dropKey();
        }
    }
}
