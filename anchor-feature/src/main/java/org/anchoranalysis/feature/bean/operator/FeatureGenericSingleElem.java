/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A single-element feature that accepts the most generic of parameters {#link {@link FeatureInput}}
 *
 * @author Owen Feehan
 * @param T input-type
 */
public abstract class FeatureGenericSingleElem<T extends FeatureInput>
        extends FeatureSingleElem<T, T> {

    public FeatureGenericSingleElem() {
        super();
    }

    public FeatureGenericSingleElem(Feature<T> feature) {
        super(feature);
    }
}
