/* (C)2020 */
package org.anchoranalysis.feature.session.calculator;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Calculates one or more features for given params
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public interface FeatureCalculatorMulti<T extends FeatureInput> {

    /** Performs one calculation throwing an exception if something goes wrong */
    ResultsVector calc(T input) throws FeatureCalcException;

    /**
     * Performs one calculation on a sub-set of the feature list throwing an exception if something
     * goes wrong
     */
    ResultsVector calc(T input, FeatureList<T> featuresSubset) throws FeatureCalcException;

    /**
     * Performs one calculation recording the error to an ErrorReporter if anything goes wrong, but
     * throwing no exception
     */
    ResultsVector calcSuppressErrors(T input, ErrorReporter errorReporter);

    /**
     * Performs one calculation, either calling {@link #calc(T)} or {@link #calcSuppressErrors}
     * depending on a flag
     *
     * @throws FeatureCalcException if suppress errors is FALSE and an error occurs during
     *     calculation
     */
    default ResultsVector calc(T input, ErrorReporter errorReporter, boolean suppressErrors)
            throws FeatureCalcException {
        if (suppressErrors) {
            return calcSuppressErrors(input, errorReporter);
        } else {
            return calc(input);
        }
    }

    /**
     * The number of features that is calculated on each call to calc(), and therefore the size of
     * the ResultsVector returned
     */
    int sizeFeatures();
}
