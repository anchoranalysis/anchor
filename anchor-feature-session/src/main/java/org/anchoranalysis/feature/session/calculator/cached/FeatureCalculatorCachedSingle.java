/* (C)2020 */
package org.anchoranalysis.feature.session.calculator.cached;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMultiFromSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;

/**
 * A {@link FeatureCalculatorSingle} but calculations are cached to avoid repetition if equal {@link
 * FeatureInput} are passed.
 *
 * @author Owen Feehan
 */
public class FeatureCalculatorCachedSingle<T extends FeatureInput>
        implements FeatureCalculatorSingle<T> {

    private final FeatureCalculatorCachedMulti<T> delegate;

    /**
     * Creates a feature-calculator with a new cache
     *
     * @param source the underlying feature-calculator to use for calculating unknown results
     * @param cacheSize size of cache
     */
    public FeatureCalculatorCachedSingle(FeatureCalculatorSingle<T> source) {
        delegate =
                new FeatureCalculatorCachedMulti<>(new FeatureCalculatorMultiFromSingle<>(source));
    }

    /**
     * Creates a feature-calculator with a new cache
     *
     * @param source the underlying feature-calculator to use for calculating unknown results
     * @param suppressErrors
     * @param cacheSize size of cache
     */
    public FeatureCalculatorCachedSingle(FeatureCalculatorSingle<T> source, int cacheSize) {
        delegate =
                new FeatureCalculatorCachedMulti<>(
                        new FeatureCalculatorMultiFromSingle<>(source), cacheSize);
    }

    @Override
    public double calc(T input) throws FeatureCalcException {
        return delegate.calc(input).get(0);
    }

    @Override
    public double calcSuppressErrors(T input, ErrorReporter errorReporter) {
        return delegate.calcSuppressErrors(input, errorReporter).get(0);
    }
}
