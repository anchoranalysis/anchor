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

import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorCachedMulti;

/**
 * A {@link FeatureCalculatorSingle} but calculations are cached to avoid repetition if equal {@link
 * FeatureInput} are passed.
 *
 * @author Owen Feehan
 */
public class FeatureCalculatorCachedSingle<T extends FeatureInput>
        implements FeatureCalculatorSingle<T> {

    private final FeatureCalculatorCachedMulti<T> delegate;

    /**
     * Creates a feature-calculator with a new cache
     *
     * @param source the underlying feature-calculator to use for calculating unknown results
     */
    public FeatureCalculatorCachedSingle(FeatureCalculatorSingle<T> source) {
        delegate =
                new FeatureCalculatorCachedMulti<>(new MultiFromSingle<>(source));
    }

    /**
     * Creates a feature-calculator with a new cache
     *
     * @param source the underlying feature-calculator to use for calculating unknown results
     * @param cacheSize size of cache
     */
    public FeatureCalculatorCachedSingle(FeatureCalculatorSingle<T> source, int cacheSize) {
        delegate =
                new FeatureCalculatorCachedMulti<>(
                        new MultiFromSingle<>(source), cacheSize);
    }

    @Override
    public double calculate(T input) throws FeatureCalculationException {
        try {
            return delegate.calculate(input).get(0);
        } catch (NamedFeatureCalculateException e) {
            throw e.dropKey();
        }
    }

    @Override
    public double calculateSuppressErrors(T input, ErrorReporter errorReporter) {
        return delegate.calculateSuppressErrors(input, errorReporter).get(0);
    }
}
