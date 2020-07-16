/* (C)2020 */
package org.anchoranalysis.bean.shared.params.keyvalue;

import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.shared.params.ParamsBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.params.KeyValueParams;

public abstract class KeyValueParamsProvider extends ParamsBean<KeyValueParamsProvider>
        implements Provider<KeyValueParams> {

    @Override
    public abstract KeyValueParams create() throws CreateException;
}
