/* (C)2020 */
package org.anchoranalysis.bean.init.property;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

/**
 * @author Owen Feehan
 * @param <P> init-param type
 */
public interface PropertyDefiner {

    boolean accepts(Class<?> paramType);

    void doInitFor(Object propertyValue, Object param, Object parent, Logger logger)
            throws InitException;

    String describeAcceptedClasses();
}
