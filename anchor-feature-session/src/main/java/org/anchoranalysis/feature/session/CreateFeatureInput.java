/* (C)2020 */
package org.anchoranalysis.feature.session;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public interface CreateFeatureInput<T extends FeatureInput> {

    T createForFeature(Feature<?> feature) throws CreateException;
}
