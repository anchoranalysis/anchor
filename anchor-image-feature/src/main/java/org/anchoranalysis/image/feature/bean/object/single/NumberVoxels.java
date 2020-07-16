/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.single;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.object.calculation.CalculateNumVoxels;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

public class NumberVoxels extends FeatureSingleObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private boolean mip = false;
    // END BEAN PROPERTIES

    @Override
    public double calc(SessionInput<FeatureInputSingleObject> input) throws FeatureCalcException {
        return input.calc(new CalculateNumVoxels(mip));
    }
}
