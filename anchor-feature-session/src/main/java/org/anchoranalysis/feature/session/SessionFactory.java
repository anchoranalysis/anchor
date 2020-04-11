package org.anchoranalysis.feature.session;

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
