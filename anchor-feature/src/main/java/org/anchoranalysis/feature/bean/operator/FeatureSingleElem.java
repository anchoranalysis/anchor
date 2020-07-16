/* (C)2020 */
package org.anchoranalysis.feature.bean.operator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A feature that contains another feature as a property (the single element)
 *
 * @author Owen Feehan
 * @param <T> input-type used for calculating feature
 * @param <S> input-type used for the "item" that is the single element
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class FeatureSingleElem<T extends FeatureInput, S extends FeatureInput>
        extends Feature<T> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private Feature<S> item = null;
    // END BEAN PARAMETERS

    @Override
    public Class<? extends FeatureInput> inputType() {
        return item.inputType();
    }
}
