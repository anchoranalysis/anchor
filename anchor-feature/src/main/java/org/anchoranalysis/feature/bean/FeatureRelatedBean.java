/* (C)2020 */
package org.anchoranalysis.feature.bean;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;

/**
 * Beans-related to features that require initialization with {@link SharedFeaturesInitParams}
 *
 * @author Owen Feehan
 * @param <T> bean-type
 */
public abstract class FeatureRelatedBean<T> extends InitializableBean<T, SharedFeaturesInitParams> {

    protected FeatureRelatedBean() {
        super(
                new PropertyInitializer<>(SharedFeaturesInitParams.class, paramExtracters()),
                new SimplePropertyDefiner<SharedFeaturesInitParams>(
                        SharedFeaturesInitParams.class));
    }

    private static List<ExtractFromParam<SharedFeaturesInitParams, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractFromParam<>(
                        KeyValueParamsInitParams.class, SharedFeaturesInitParams::getParams));
    }
}
