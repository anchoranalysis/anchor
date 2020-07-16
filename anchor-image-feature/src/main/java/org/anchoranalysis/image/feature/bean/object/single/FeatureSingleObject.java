/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.single;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

public abstract class FeatureSingleObject extends Feature<FeatureInputSingleObject> {

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputSingleObject.class;
    }
}
