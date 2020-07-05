package org.anchoranalysis.feature.session;

import java.util.Optional;

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
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingleFromMulti;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FeatureSession {
	
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
			new SharedFeatureMulti(),
			logger
		);
	}
	
	
	/**
	 * Starts a feature-session for a single feature
	 * 
	 * @param <T> type of parameters
	 * @param feature the feature
	 * @param sharedFeatures
	 * @param logger a logger
	 * @return a calculator that will calculate just this feature for each parameter.
	 * @throws FeatureCalcException
	 */
	public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
		Feature<T> feature,
		SharedFeatureMulti sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return with(
			feature,
			new FeatureInitParams(),
			sharedFeatures,
			logger
		);
	}
	

	public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
		Feature<T> feature,
		FeatureInitParams initParams,
		SharedFeatureMulti sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		SequentialSession<T> session = new SequentialSession<>(feature); 
		startSession(
			session,
			initParams,
			sharedFeatures,
			logger
		);			
		return new FeatureCalculatorSingleFromMulti<>(session);
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
			new SharedFeatureMulti(),
			logger				
		);
	}
	
	
	public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
		FeatureList<T> features,
		FeatureInitParams initParams,
		SharedFeatureMulti sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return with(
			features,
			initParams,
			Optional.of(sharedFeatures),
			logger,
			new BoundReplaceStrategy<>(ReuseSingletonStrategy::new)
		);
	}
	
	public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
		FeatureList<T> features,
		FeatureInitParams initParams,
		Optional<SharedFeatureMulti> sharedFeatures,
		LogErrorReporter logger,
		BoundReplaceStrategy<T,? extends ReplaceStrategy<T>> replacePolicyFactory
	) throws FeatureCalcException {
		SequentialSession<T> session = new SequentialSession<>(features, replacePolicyFactory); 
		startSession(
			session,
			initParams,
			sharedFeatures.orElse( new SharedFeatureMulti() ),
			logger
		);
		return session;
	}
	
	private static <T extends FeatureInput> void startSession(
		SequentialSession<T> session,
		FeatureInitParams initParams,
		SharedFeatureMulti sharedFeatures,
		LogErrorReporter logger
	) throws FeatureCalcException {
		try {
			session.start( initParams, sharedFeatures, logger );
		} catch (InitException e) {
			throw new FeatureCalcException("An error occurred starting the feature (sequential) session", e);
		}
	}
}
