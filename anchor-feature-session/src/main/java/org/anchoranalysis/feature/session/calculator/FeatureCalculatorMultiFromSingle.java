package org.anchoranalysis.feature.session.calculator;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureCalculatorMultiFromSingle<T extends FeatureInput> implements FeatureCalculatorMulti<T> {

	private FeatureCalculatorSingle<T> delegate;
	
	public FeatureCalculatorMultiFromSingle(FeatureCalculatorSingle<T> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public ResultsVector calc(T input) throws FeatureCalcException {
		return vectorFor(
			delegate.calc(input)	
		);
	}

	@Override
	public ResultsVector calc(T input, FeatureList<T> featuresSubset) throws FeatureCalcException {
		throw new FeatureCalcException("This operation is not supported");
	}

	@Override
	public ResultsVector calcSuppressErrors(T input, ErrorReporter errorReporter) {
		return vectorFor(
			delegate.calcSuppressErrors(input, errorReporter)	
		);
	}

	@Override
	public int sizeFeatures() {
		return 1;
	}
	
	private static ResultsVector vectorFor( double result ) {
		ResultsVector out = new ResultsVector(1);
		out.set(0, result);
		return out;
	}
}
