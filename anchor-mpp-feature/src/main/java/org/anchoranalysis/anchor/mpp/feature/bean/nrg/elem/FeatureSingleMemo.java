/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FeatureSingleMemo extends Feature<FeatureInputSingleMemo> {

    @Override
    public abstract double calc(SessionInput<FeatureInputSingleMemo> input)
            throws FeatureCalcException;

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputSingleMemo.class;
    }
}
