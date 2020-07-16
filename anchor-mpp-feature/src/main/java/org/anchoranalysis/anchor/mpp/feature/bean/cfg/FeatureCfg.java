/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.cfg;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FeatureCfg extends Feature<FeatureInputCfg> {

    @Override
    public double calc(SessionInput<FeatureInputCfg> input) throws FeatureCalcException {
        return calc(input.get());
    }

    // Calculates an NRG element for a set of pixels
    public abstract double calc(FeatureInputCfg params) throws FeatureCalcException;

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputCfg.class;
    }
}
