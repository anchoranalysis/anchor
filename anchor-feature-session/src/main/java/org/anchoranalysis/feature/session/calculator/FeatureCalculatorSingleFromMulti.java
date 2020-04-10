package org.anchoranalysis.feature.session.calculator;

/*-
 * #%L
 * anchor-feature-session
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * Exposes a FeatureCalculatorMulti as a FeatureCalculatorSingle
 * @author owen
 *
 * @param <T> feature-params type
 */
public class FeatureCalculatorSingleFromMulti<T extends FeatureCalcParams> implements FeatureCalculatorSingle<T> {

	private FeatureCalculatorMulti<T> delegate;
		
	public FeatureCalculatorSingleFromMulti(FeatureCalculatorMulti<T> multi) throws CreateException {
		super();
		this.delegate = multi;
		if (delegate.sizeFeatures()!=1) {
			throw new CreateException(
				String.format("When creating a %s, the multi must have exactly one feature")
			);
		}
	}

	@Override
	public double calcSuppressErrors(T params, ErrorReporter errorReporter) {
		return delegate.calcSuppressErrors(params, errorReporter).get(0);
	}

	@Override
	public double calc(T params) throws FeatureCalcException {
		return delegate.calc(params).get(0);
	}

}
