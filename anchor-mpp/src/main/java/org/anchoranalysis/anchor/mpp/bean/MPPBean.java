/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.init.PointsInitParams;
import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;

public abstract class MPPBean<T> extends InitializableBean<T, MPPInitParams> {

    protected MPPBean() {
        super(initializerForMPPBeans(), new SimplePropertyDefiner<>(MPPInitParams.class));
    }

    /**
     * Creates a property-initializes for MPP-Beans
     *
     * <p>Beware concuirrency. Initializers are stateful with the {#link {@link
     * PropertyInitializer#setParam(Object)} method so this should be created newly for each thread,
     * rather reused statically
     *
     * @return
     */
    public static PropertyInitializer<MPPInitParams> initializerForMPPBeans() {
        return new PropertyInitializer<>(MPPInitParams.class, paramExtracters());
    }

    private static List<ExtractFromParam<MPPInitParams, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractFromParam<>(PointsInitParams.class, MPPInitParams::getPoints),
                new ExtractFromParam<>(SharedFeaturesInitParams.class, MPPInitParams::getFeature),
                new ExtractFromParam<>(KeyValueParamsInitParams.class, MPPInitParams::getParams),
                new ExtractFromParam<>(ImageInitParams.class, MPPInitParams::getImage));
    }
}
