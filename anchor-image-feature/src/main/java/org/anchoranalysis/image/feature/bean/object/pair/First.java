/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.pair;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

/**
 * Evaluates the first-object in a pair only
 *
 * @author Owen Feehan
 */
public class First extends FeatureDeriveFromPair {

    public First() {
        // BEAN Constructor
    }

    public First(Feature<FeatureInputSingleObject> item) {
        super(item);
    }

    @Override
    public double calc(SessionInput<FeatureInputPairObjects> params) throws FeatureCalcException {
        return valueFromFirst(params);
    }
}
