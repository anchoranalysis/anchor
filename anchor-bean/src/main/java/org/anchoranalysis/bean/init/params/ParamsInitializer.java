/* (C)2020 */
package org.anchoranalysis.bean.init.params;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

/**
 * @author Owen Feehan
 * @param <T> init-params type
 */
@FunctionalInterface
public interface ParamsInitializer<T> {

    void init(T so, Logger logger) throws InitException;
}
