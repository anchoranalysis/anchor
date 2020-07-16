/* (C)2020 */
package org.anchoranalysis.feature.bean.provider;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureProviderDefine<T extends FeatureInput> extends FeatureProvider<T> {

    // START BEAN PROPERTIES
    @BeanField @SkipInit private Feature<T> feature;
    // END BEAN PROPERTIES

    public FeatureProviderDefine() {}

    public FeatureProviderDefine(Feature<T> feature) {
        this.feature = feature;
    }

    @Override
    public Feature<T> create() throws CreateException {
        return feature;
    }

    public Feature<T> getFeature() {
        return feature;
    }

    public void setFeature(Feature<T> feature) {
        this.feature = feature;
    }
}
