package org.anchoranalysis.feature.session;

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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingleFromMulti;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class SessionFactory {

	public static <T extends FeatureCalcParams> FeatureCalculatorSingle<T> createAndStart(
		Feature<T> feature,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return createAndStart(
			feature,
			new FeatureInitParams(),
			new SharedFeatureSet<T>(),
			logger
		);
	}
	
	public static <T extends FeatureCalcParams> FeatureCalculatorMulti<T> createAndStart(
		FeatureList<T> features,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return createAndStart(
			features,
			new FeatureInitParams(),
			new SharedFeatureSet<T>(),
			logger				
		);
	}
	
	public static <T extends FeatureCalcParams> FeatureCalculatorSingle<T> createAndStart(
		Feature<T> feature,
		FeatureInitParams initParams,
		SharedFeatureSet<T> sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		SequentialSession<T> session = new SequentialSession<>(feature); 
		startSession(session, initParams, sharedFeatures, logger);			
		return new FeatureCalculatorSingleFromMulti<>(session);
	}
	
	
	public static <T extends FeatureCalcParams> FeatureCalculatorMulti<T> createAndStart(
		FeatureList<T> features,
		FeatureInitParams initParams,
		SharedFeatureSet<T> sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		SequentialSession<T> session = new SequentialSession<>(features); 
		startSession(session, initParams, sharedFeatures, logger);
		return session;
	}
	
	private static <T extends FeatureCalcParams> void startSession(
		SequentialSession<T> session,
		FeatureInitParams initParams,
		SharedFeatureSet<T> sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		try {
			session.start( initParams, sharedFeatures, logger );
		} catch (InitException e) {
			throw new FeatureCalcException("An error occurred starting the feature (sequential) session", e);
		}
	}
}
