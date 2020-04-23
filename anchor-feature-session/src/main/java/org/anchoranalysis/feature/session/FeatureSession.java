package org.anchoranalysis.feature.session;

import java.util.ArrayList;
import java.util.Collection;

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
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingleFromMulti;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * A single-point in the code for creating feature-sessions (a factory).
 * 
 * <p>A feature session is a context needed to calculate one or more parameters (inptus to features) on one or more features</p>
 * 
 * <p>Within this context, caching of intermediate results and other efficiencies are implemented beneath the hood.</p>
 * 
 * @author Owen Feehan
 *
 */
public class FeatureSession {

	private FeatureSession() {}
	
	/**
	 * Starts a feature-session for a single feature
	 * 
	 * @param <T> type of parameters
	 * @param feature the feature
	 * @param logger a logger
	 * @return a calculator that will calculate just this feature for each parameter.
	 * @throws FeatureCalcException
	 */
	public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
		Feature<T> feature,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return with(
			feature,
			new FeatureInitParams(),
			new SharedFeatureSet<T>(),
			logger
		);
	}

	/**
	 * Starts a feature-session for a list of features
	 * 
	 * @param <T> type of parameters for all features
	 * @param features a list of features accepting uniform type
	 * @param logger a logger
	 * @return a calculator that will call calculate all the features in the list for each parameter.
	 * @throws FeatureCalcException
	 */
	public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
		FeatureList<T> features,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return with(
			features,
			new FeatureInitParams(),
			new SharedFeatureSet<T>(),
			logger				
		);
	}
	
	public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
		Feature<T> feature,
		FeatureInitParams initParams,
		SharedFeatureSet<T> sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		SequentialSession<T> session = new SequentialSession<>(feature); 
		startSession(session, initParams, sharedFeatures, logger);			
		return new FeatureCalculatorSingleFromMulti<>(session);
	}
	
	public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
		FeatureList<T> features,
		FeatureInitParams initParams,
		SharedFeatureSet<T> sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return with(features, initParams, sharedFeatures, logger, new ArrayList<>() );
	}
	
	public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
		FeatureList<T> features,
		FeatureInitParams initParams,
		SharedFeatureSet<T> sharedFeatures,
		LogErrorReporter logger,
		Collection<String> ignoreFeaturePrefixes
	) throws FeatureCalcException {
		SequentialSession<T> session = new SequentialSession<>(features, ignoreFeaturePrefixes); 
		startSession(session, initParams, sharedFeatures, logger);
		return session;
	}
	
	private static <T extends FeatureInput> void startSession(
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
