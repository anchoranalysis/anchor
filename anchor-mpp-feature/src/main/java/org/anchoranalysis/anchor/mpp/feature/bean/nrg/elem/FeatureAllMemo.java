/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FeatureAllMemo extends Feature<FeatureInputAllMemo> {

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputAllMemo.class;
    }
}
