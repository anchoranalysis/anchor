/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.points;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.anchor.mpp.bean.init.PointsInitParams;
import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;

/**
 * @author Owen Feehan
 * @param <T> bean-type
 */
public abstract class PointsBean<T> extends InitializableBean<T, PointsInitParams> {

    protected PointsBean() {
        super(
                new PropertyInitializer<>(PointsInitParams.class, paramExtracters()),
                new SimplePropertyDefiner<PointsInitParams>(PointsInitParams.class));
    }

    private static List<ExtractFromParam<PointsInitParams, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractFromParam<>(ImageInitParams.class, PointsInitParams::getImage));
    }
}
