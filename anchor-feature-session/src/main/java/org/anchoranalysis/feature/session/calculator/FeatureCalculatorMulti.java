package org.anchoranalysis.feature.session.calculator;

import java.util.List;

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

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * Calculates one or more features for given params
 * 
 * @author owen
 *
 * @param <T>
 */
public interface FeatureCalculatorMulti<T extends FeatureCalcParams> {

	/* Does one calculation recording the error to an ErrorReporter if anything goes wrong, but throwing no exception */
	ResultsVector calcOneSuppressErrors(T params, ErrorReporter errorReporter );
	
	/* Does one calculation throwing an exception if something goes wrong */
	ResultsVector calcOne( T params ) throws FeatureCalcException;
	
	/**
	 * Calculates many parameters on each feature.
	 * 
	 * @param listParams a list of parameters the same size as the features
	 * @return a results vector, one result for each feature
	 * @throws FeatureCalcException
	 */
	List<ResultsVector> calcMany( List<T> listParams ) throws FeatureCalcException;
	
	/** The number of features that is calculated on each call to calc(), and therefore the size of the ResultsVector returned */
	int sizeFeatures();
}
