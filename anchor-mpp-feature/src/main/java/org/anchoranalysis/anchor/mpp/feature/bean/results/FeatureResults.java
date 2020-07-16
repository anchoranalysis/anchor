/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.results;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResults;

/**
 * Features that process {@link ResultsVectorCollection} i.e. the result of the calculation of some
 * other features.
 *
 * <p>This is useful for applying some aggregate statistics (min, max etc.) to the results of
 * multiple features calculated together.
 *
 * @author Owen Feehan
 */
public abstract class FeatureResults extends Feature<FeatureInputResults> {

    @Override
    public double calc(SessionInput<FeatureInputResults> input) throws FeatureCalcException {
        return calc(input.get());
    }

    // Calculates an NRG element for a set of pixels
    public abstract double calc(FeatureInputResults input) throws FeatureCalcException;

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputResults.class;
    }
}
