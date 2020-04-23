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
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureInput;

/**
 * Likes a SequentialSession but automatically changes parameters before calculation
 *
 * @author owen
 *
 * @param <T> feature-input
 */
public class FeatureCalculatorMultiChangeParams<T extends FeatureInput> implements FeatureCalculatorMulti<T> {

	private FeatureCalculatorMulti<T> calculator;
	private Consumer<T> changeParams;
	
	public FeatureCalculatorMultiChangeParams(FeatureCalculatorMulti<T> calculator, Consumer<T> changeParams) {
		this.calculator = calculator;
		this.changeParams = changeParams;
	}

	public ResultsVector calc(T params) throws FeatureCalcException {
		changeParams.accept(params);
		return calculator.calc(params);
	}
	

	@Override
	public ResultsVector calc(T params, FeatureList<T> featuresSubset) throws FeatureCalcException {
		changeParams.accept(params);
		return calculator.calc(params, featuresSubset);
	}

	public ResultsVector calcSuppressErrors(T params, ErrorReporter errorReporter) {
		changeParams.accept(params);
		return calculator.calcSuppressErrors(
			params,
			errorReporter
		);
	}
	
	@Override
	public int sizeFeatures() {
		return calculator.sizeFeatures();
	}
}
