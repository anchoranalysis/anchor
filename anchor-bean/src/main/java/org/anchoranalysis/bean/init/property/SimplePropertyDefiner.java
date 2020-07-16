/* (C)2020 */
package org.anchoranalysis.bean.init.property;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.bean.init.params.ParamsInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

/**
 * @author Owen Feehan
 * @param <P> param type
 */
@RequiredArgsConstructor
public class SimplePropertyDefiner<P> implements PropertyDefiner {

    private final Class<?> paramTypeMatch;

    @Override
    public boolean accepts(Class<?> paramType) {
        return paramTypeMatch.isAssignableFrom(paramType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doInitFor(Object propertyValue, Object param, Object parent, Logger logger)
            throws InitException {

        if (!(propertyValue instanceof ParamsInitializer)) {
            throw new InitException(
                    String.format(
                            "propertyValue is not an instance of %s, as is required for this property-definer",
                            ParamsInitializer.class.getSimpleName()));
        }

        if (!paramTypeMatch.isAssignableFrom(param.getClass())) {
            throw new InitException(
                    String.format(
                            "param is not the same class or a subclass of %s, as is required for this property-definer",
                            paramTypeMatch.getSimpleName()));
        }

        ParamsInitializer<P> propertyValueCast = (ParamsInitializer<P>) propertyValue;
        propertyValueCast.init((P) param, logger);
    }

    @Override
    public String toString() {
        return String.format("simpleProp=%s", paramTypeMatch);
    }

    @Override
    public String describeAcceptedClasses() {
        return paramTypeMatch.getSimpleName();
    }
}
