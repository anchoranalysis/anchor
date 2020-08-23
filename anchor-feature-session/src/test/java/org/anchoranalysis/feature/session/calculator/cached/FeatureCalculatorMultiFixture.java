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

package org.anchoranalysis.feature.session.calculator.cached;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

class FeatureCalculatorMultiFixture {

    /**
     * Creates a feature-calculator than returns a constant result
     */
    public static <T extends FeatureInput> FeatureCalculatorMulti<T> createFeatureCalculator(
            ResultsVector rv) {

        try {
            @SuppressWarnings("unchecked")
            FeatureCalculatorMulti<T> calculator = mock(FeatureCalculatorMulti.class);
            when(calculator.calculate(any())).thenReturn(rv);
            when(calculator.calculateSuppressErrors(any(), any())).thenReturn(rv);
            return calculator;
        } catch (NamedFeatureCalculateException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
    }
}
