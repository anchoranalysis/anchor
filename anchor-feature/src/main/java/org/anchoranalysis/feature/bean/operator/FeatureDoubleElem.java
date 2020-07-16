/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.descriptor.FeatureInputType;

public abstract class FeatureDoubleElem<T extends FeatureInput> extends Feature<T> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private Feature<T> item1 = null;

    @BeanField @Getter @Setter private Feature<T> item2 = null;
    // END BEAN PARAMETERS

    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputType.prefer(item1, item2);
    }
}
