/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.pair;

import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;

/**
 * Subtract second-object from first-object in a pair
 *
 * @author Owen Feehan
 */
public class SubtractSecondFromFirst extends FeatureDeriveFromPair {

    @Override
    public double calc(SessionInput<FeatureInputPairObjects> params) throws FeatureCalcException {
        return valueFromFirst(params) - valueFromSecond(params);
    }
}
