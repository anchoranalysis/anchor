/* (C)2020 */
package org.anchoranalysis.bean;

import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.params.NullInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

/**
 * @author Owen Feehan
 * @param <T> bean-type
 */
public abstract class NullParamsBean<T> extends InitializableBean<T, NullInitParams> {

    protected NullParamsBean() {
        super(
                new PropertyInitializer<NullInitParams>(NullInitParams.class),
                new SimplePropertyDefiner<NullInitParams>(NullInitParams.class));
    }

    @Override
    public final void onInit(NullInitParams so) throws InitException {
        onInit();
    }

    /** As there's no parameters we expose a different method */
    public void onInit() throws InitException {
        // NOTHING TO DO. This method exists so it can be overrided as needed in sub-classes.
    }

    public void initRecursive(Logger logger) throws InitException {
        super.initRecursive(NullInitParams.instance(), logger);
    }
}
