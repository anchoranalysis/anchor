/* (C)2020 */
package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.FeatureSymbolCalculator;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Calculates features in the context of a particular {#FeatureSessionCache}.
 *
 * @author Owen Feehan
 * @param T feature-input type that the cache supports
 */
public interface FeatureSessionCacheCalculator<T extends FeatureInput>
        extends CalculationResolver<T>, FeatureSymbolCalculator<T> {

    /**
     * Calculate a feature with particular values
     *
     * @param feature feature
     * @param input feature-input
     * @return the feature-value
     * @throws FeatureCalcException
     */
    double calc(Feature<T> feature, SessionInput<T> input) throws FeatureCalcException;

    /**
     * Calculates a feature-list throwing an exception if there is an error
     *
     * @param features list of features
     * @param input params
     * @return the results of each feature, with Double.NaN (and the stored exception) if an error
     *     occurs
     * @throws FeatureCalcException
     */
    default ResultsVector calc(FeatureList<T> features, SessionInput<T> input)
            throws FeatureCalcException {
        ResultsVector out = new ResultsVector(features.size());
        for (int i = 0; i < features.size(); i++) {

            Feature<T> f = features.get(i);

            try {
                double val = calc(f, input);
                out.set(i, val);
            } catch (FeatureCalcException e) {

                throw new FeatureCalcException(
                        String.format("Feature '%s' has thrown an error%n", f.getFriendlyName()),
                        e);
            }
        }
        return out;
    }
}
