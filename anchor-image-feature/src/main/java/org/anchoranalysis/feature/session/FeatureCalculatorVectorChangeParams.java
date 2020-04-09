package org.anchoranalysis.feature.session;

import java.util.function.Consumer;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.image.feature.stack.nrg.FeatureNRGStackParams;

/**
 * Likes a SequentialSession but automatically changes parameters before calculation
 *
 * @author owen
 *
 * @param <T> feature-calc-params
 */
public class FeatureCalculatorVectorChangeParams<T extends FeatureCalcParams> implements FeatureCalculatorVector<T> {

	private FeatureCalculatorVector<T> calculator;
	private Consumer<T> changeParams;
	
	public FeatureCalculatorVectorChangeParams(FeatureCalculatorVector<T> calculator, Consumer<T> changeParams) {
		this.calculator = calculator;
		this.changeParams = changeParams;
	}

	public ResultsVector calc(T params) throws FeatureCalcException {
		changeParams.accept(params);
		return calculator.calc(params);
	}

	public ResultsVector calcSuppressErrors(T params, ErrorReporter errorReporter) {
		changeParams.accept(params);
		return calculator.calcSuppressErrors(
			params,
			errorReporter
		);
	}
}
