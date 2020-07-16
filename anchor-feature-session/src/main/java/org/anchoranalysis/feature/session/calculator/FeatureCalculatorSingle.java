/* (C)2020 */
package org.anchoranalysis.feature.session.calculator;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Calculates the result of a feature for particular params
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public interface FeatureCalculatorSingle<T extends FeatureInput> {

    /** Performs one calculation throwing an exception if something goes wrong */
    double calc(T input) throws FeatureCalcException;

    /**
     * Performs one calculation recording the error to an ErrorReporter if anything goes wrong, but
     * throwing no exception
     */
    double calcSuppressErrors(T input, ErrorReporter errorReporter);
}
