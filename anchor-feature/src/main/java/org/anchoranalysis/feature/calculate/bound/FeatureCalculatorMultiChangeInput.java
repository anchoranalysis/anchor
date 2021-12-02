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

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * A {@link FeatureCalculatorMulti} but changes the input before calculation.
 *
 * @author Owen Feehan
 * @param <T> feature-input-type
 */
@AllArgsConstructor
public class FeatureCalculatorMultiChangeInput<T extends FeatureInput>
        implements FeatureCalculatorMulti<T> {

    /** Delegate that is called after an input is changed. */
    private FeatureCalculatorMulti<T> calculator;

    /** A function that is applied to change the input before being passed to the delegate. */
    private Consumer<T> change;

    public ResultsVector calculate(T input) throws NamedFeatureCalculateException {
        change.accept(input);
        return calculator.calculate(input);
    }

    @Override
    public ResultsVector calculate(T input, FeatureList<T> featuresSubset)
            throws NamedFeatureCalculateException {
        change.accept(input);
        return calculator.calculate(input, featuresSubset);
    }

    public ResultsVector calculateSuppressErrors(T input, ErrorReporter errorReporter) {
        change.accept(input);
        return calculator.calculateSuppressErrors(input, errorReporter);
    }

    @Override
    public int sizeFeatures() {
        return calculator.sizeFeatures();
    }
}
