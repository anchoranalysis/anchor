/* (C)2020 */
package org.anchoranalysis.image.feature.bean.stack;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

public abstract class FeatureStack extends Feature<FeatureInputStack> {

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputStack.class;
    }
}
