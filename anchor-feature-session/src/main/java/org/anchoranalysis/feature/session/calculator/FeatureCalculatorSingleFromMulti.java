/* (C)2020 */
package org.anchoranalysis.feature.session.calculator;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Exposes a {@link FeatureCalculatorMulti} as a {@link FeatureCalculatorSingle}
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public class FeatureCalculatorSingleFromMulti<T extends FeatureInput>
        implements FeatureCalculatorSingle<T> {

    private FeatureCalculatorMulti<T> delegate;

    public FeatureCalculatorSingleFromMulti(FeatureCalculatorMulti<T> multi)
            throws FeatureCalcException {
        super();
        this.delegate = multi;
        if (delegate.sizeFeatures() != 1) {
            throw new FeatureCalcException(
                    String.format(
                            "When creating a %s, the multi must have exactly one feature",
                            FeatureCalculatorSingle.class.getSimpleName()));
        }
    }

    @Override
    public double calcSuppressErrors(T input, ErrorReporter errorReporter) {
        return delegate.calcSuppressErrors(input, errorReporter).get(0);
    }

    @Override
    public double calc(T params) throws FeatureCalcException {
        return delegate.calc(params).get(0);
    }
}
