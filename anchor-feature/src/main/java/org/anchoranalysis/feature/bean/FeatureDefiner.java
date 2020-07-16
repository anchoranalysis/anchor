/* (C)2020 */
package org.anchoranalysis.feature.bean;

import org.anchoranalysis.bean.init.property.PropertyDefiner;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;

class FeatureDefiner<T extends FeatureInput> implements PropertyDefiner {

    @Override
    public boolean accepts(Class<?> paramType) {
        return FeatureInitParams.class.isAssignableFrom(paramType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doInitFor(Object propertyValue, Object params, Object parent, Logger logger)
            throws InitException {

        if (parent != null && !(parent instanceof Feature)) {
            throw new InitException("A feature may only have another feature as a bean-parent");
        }

        if (propertyValue instanceof Feature) {
            Feature<T> propertyValueCast = (Feature<T>) propertyValue;
            propertyValueCast.init((FeatureInitParams) params, logger);
        }
    }

    @Override
    public String describeAcceptedClasses() {
        return FeatureInitParams.class.getSimpleName();
    }
}
