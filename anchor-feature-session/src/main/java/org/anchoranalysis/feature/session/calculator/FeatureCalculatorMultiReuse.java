/* (C)2020 */
package org.anchoranalysis.feature.session.calculator;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Remembers the first-calculation and always returns this value for all subsequent calculations
 *
 * @author Owen Feehan
 */
public class FeatureCalculatorMultiReuse<T extends FeatureInput>
        implements FeatureCalculatorMulti<T> {

    private FeatureCalculatorMulti<T> delegate;

    private ResultsVector rv = null;

    public FeatureCalculatorMultiReuse(FeatureCalculatorMulti<T> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public ResultsVector calcSuppressErrors(T params, ErrorReporter errorReporter) {
        if (rv == null) {
            rv = delegate.calcSuppressErrors(params, errorReporter);
        }
        return rv;
    }

    @Override
    public ResultsVector calc(T params) throws FeatureCalcException {
        if (rv == null) {
            rv = delegate.calc(params);
        }
        return rv;
    }

    @Override
    public ResultsVector calc(T params, FeatureList<T> featuresSubset) throws FeatureCalcException {
        throw new FeatureCalcException("This operation is not supported");
    }

    @Override
    public int sizeFeatures() {
        return delegate.sizeFeatures();
    }
}
