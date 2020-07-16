/* (C)2020 */
package org.anchoranalysis.bean.shared.params;

import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;

public abstract class ParamsBean<T> extends InitializableBean<T, KeyValueParamsInitParams> {

    protected ParamsBean() {
        super(
                new PropertyInitializer<>(KeyValueParamsInitParams.class),
                new SimplePropertyDefiner<>(KeyValueParamsInitParams.class));
    }
}
