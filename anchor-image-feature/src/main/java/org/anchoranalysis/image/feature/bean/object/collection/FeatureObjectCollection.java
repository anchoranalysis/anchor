/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.collection;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.object.input.FeatureInputObjectCollection;

public abstract class FeatureObjectCollection extends Feature<FeatureInputObjectCollection> {

    @Override
    public double calc(SessionInput<FeatureInputObjectCollection> input)
            throws FeatureCalcException {
        return calc(input.get());
    }

    // Calculates an NRG element for a set of pixels
    public abstract double calc(FeatureInputObjectCollection params) throws FeatureCalcException;

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputObjectCollection.class;
    }
}
