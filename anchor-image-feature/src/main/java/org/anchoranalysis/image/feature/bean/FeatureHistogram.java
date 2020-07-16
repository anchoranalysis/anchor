/* (C)2020 */
package org.anchoranalysis.image.feature.bean;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.histogram.FeatureInputHistogram;

public abstract class FeatureHistogram extends Feature<FeatureInputHistogram> {

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputHistogram.class;
    }
}
