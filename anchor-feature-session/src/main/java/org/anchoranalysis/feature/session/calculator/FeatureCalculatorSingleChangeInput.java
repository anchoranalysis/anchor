package org.anchoranalysis.feature.session.calculator;



/*-
 * #%L
 * anchor-image-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.function.Consumer;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Likes a SequentialSession but automatically changes parameters before calculation
 *
 * @author owen
 *
 * @param <T> feature-input
 */
public class FeatureCalculatorSingleChangeInput<T extends FeatureInput> implements FeatureCalculatorSingle<T> {

	private FeatureCalculatorSingle<T> calculator;
	private Consumer<T> funcToApplyChange;
	
	/**
	 * Constructor
	 * 
	 * @param calculator delegate which is called after an input is changed
	 * @param funcToApplyChange a function that is applied to change the input before being passed to the delegate
	 */
	public FeatureCalculatorSingleChangeInput(FeatureCalculatorSingle<T> calculator, Consumer<T> funcToApplyChange) {
		this.calculator = calculator;
		this.funcToApplyChange = funcToApplyChange;
	}

	public double calc(T input) throws FeatureCalcException {
		funcToApplyChange.accept(input);
		return calculator.calc(input);
	}

	public double calcSuppressErrors(T input, ErrorReporter errorReporter) {
		funcToApplyChange.accept(input);
		return calculator.calcSuppressErrors(input,	errorReporter);
	}
}
