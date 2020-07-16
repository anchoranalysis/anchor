/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.pair;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

/**
 * Ratio of first-object to second-object in a pair
 *
 * @author Owen Feehan
 */
public class Minimum extends FeatureDeriveFromPair {

    public Minimum() {
        // BEAN Constructor
    }

    public Minimum(Feature<FeatureInputSingleObject> item) {
        super(item);
    }

    @Override
    public double calc(SessionInput<FeatureInputPairObjects> params) throws FeatureCalcException {
        return Math.min(valueFromFirst(params), valueFromSecond(params));
    }
}
