/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FeatureMark extends Feature<FeatureInputMark> {

    @Override
    public abstract double calc(SessionInput<FeatureInputMark> input) throws FeatureCalcException;

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputMark.class;
    }
}
