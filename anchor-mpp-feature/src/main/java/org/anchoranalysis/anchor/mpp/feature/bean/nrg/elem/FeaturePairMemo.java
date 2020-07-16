/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FeaturePairMemo extends Feature<FeatureInputPairMemo> {

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputPairMemo.class;
    }
}
