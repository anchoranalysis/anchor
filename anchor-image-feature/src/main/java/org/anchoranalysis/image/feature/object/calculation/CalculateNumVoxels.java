/* (C)2020 */
package org.anchoranalysis.image.feature.object.calculation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculateNumVoxels extends FeatureCalculation<Double, FeatureInputSingleObject> {

    private final boolean mip;

    public static double calc(ObjectMask object, boolean mip) {
        if (mip) {
            object = object.maxIntensityProjection();
        }
        return object.numberVoxelsOn();
    }

    // Public, as it's needed by Mockito in test verifications
    @Override
    public Double execute(FeatureInputSingleObject params) {
        return calc(params.getObject(), mip);
    }
}
