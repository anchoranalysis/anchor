/* (C)2020 */
package org.anchoranalysis.feature.bean.provider;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FeatureProvider<T extends FeatureInput>
        extends FeatureProviderBean<FeatureProvider<T>, Feature<T>> {

    public abstract Feature<T> create() throws CreateException;
}
