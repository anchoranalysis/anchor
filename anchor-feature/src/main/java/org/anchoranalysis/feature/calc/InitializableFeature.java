/* (C)2020 */
package org.anchoranalysis.feature.calc;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A feature that should be initialized with {@link FeatureInitParams} (or a sub-class) before any
 * calculations occur.
 *
 * @author Owen Feehan
 * @param <T> input-type for feature
 */
public interface InitializableFeature<T extends FeatureInput> {

    void init(FeatureInitParams params, Feature<T> parentFeature, Logger logger)
            throws InitException;

    /**
     * A friendly name that can be displayed to user describing the Feature. Should always
     * prioritise the CustomName if one is associated with the feature
     *
     * @return
     */
    String getFriendlyName();
}
