/* (C)2020 */
package org.anchoranalysis.image.feature.bean;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputNRG;

/**
 * A base class for features that only require an input that extends from {@link FeatureInputNRG}
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public abstract class FeatureNRGStack<T extends FeatureInputNRG> extends Feature<T> {

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputNRG.class;
    }
}
