/* (C)2020 */
package org.anchoranalysis.image.bean;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;

public abstract class ImageBean<T> extends InitializableBean<T, ImageInitParams> {

    protected ImageBean() {
        super(
                new PropertyInitializer<>(ImageInitParams.class, paramExtracters()),
                new SimplePropertyDefiner<ImageInitParams>(ImageInitParams.class));
    }

    private static List<ExtractFromParam<ImageInitParams, ?>> paramExtracters() {
        return Arrays.asList(
                new ExtractFromParam<>(KeyValueParamsInitParams.class, ImageInitParams::getFeature),
                new ExtractFromParam<>(KeyValueParamsInitParams.class, ImageInitParams::getParams));
    }
}
