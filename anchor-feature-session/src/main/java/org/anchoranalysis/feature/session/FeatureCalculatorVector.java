package org.anchoranalysis.feature.session;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * Allows a vector of features to be calculated
 * 
 * @author owen
 *
 * @param <T>
 */
public interface FeatureCalculatorVector<T extends FeatureCalcParams> {

	ResultsVector calcSuppressErrors(T params, ErrorReporter errorReporter );
	
	ResultsVector calc( T params ) throws FeatureCalcException;
}
