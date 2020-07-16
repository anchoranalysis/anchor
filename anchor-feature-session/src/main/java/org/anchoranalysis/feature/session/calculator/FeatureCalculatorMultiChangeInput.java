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
/* (C)2020 */
package org.anchoranalysis.feature.session.calculator;

import java.util.function.Consumer;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Likes a SequentialSession but automatically changes parameters before calculation
 *
 * @author Owen Feehan
 * @param <T> feature-input-type
 */
public class FeatureCalculatorMultiChangeInput<T extends FeatureInput>
        implements FeatureCalculatorMulti<T> {

    private FeatureCalculatorMulti<T> calculator;
    private Consumer<T> funcToApplyChange;

    /**
     * Constructor
     *
     * @param calculator delegate which is called after an input is changed
     * @param funcToApplyChange a function that is applied to change the input before being passed
     *     to the delegate
     */
    public FeatureCalculatorMultiChangeInput(
            FeatureCalculatorMulti<T> calculator, Consumer<T> funcToApplyChange) {
        this.calculator = calculator;
        this.funcToApplyChange = funcToApplyChange;
    }

    public ResultsVector calc(T input) throws FeatureCalcException {
        funcToApplyChange.accept(input);
        return calculator.calc(input);
    }

    @Override
    public ResultsVector calc(T input, FeatureList<T> featuresSubset) throws FeatureCalcException {
        funcToApplyChange.accept(input);
        return calculator.calc(input, featuresSubset);
    }

    public ResultsVector calcSuppressErrors(T input, ErrorReporter errorReporter) {
        funcToApplyChange.accept(input);
        return calculator.calcSuppressErrors(input, errorReporter);
    }

    @Override
    public int sizeFeatures() {
        return calculator.sizeFeatures();
    }
}
