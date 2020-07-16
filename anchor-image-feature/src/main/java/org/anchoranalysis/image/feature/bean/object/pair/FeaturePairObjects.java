/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.pair;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;

public abstract class FeaturePairObjects extends Feature<FeatureInputPairObjects> {

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputPairObjects.class;
    }
}
