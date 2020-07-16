/* (C)2020 */
package org.anchoranalysis.image.feature.bean.physical.convert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.bean.orientation.DirectionVectorBean;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.DirectionVector;

//
@NoArgsConstructor
public class ConvertToPhysicalDistance<T extends FeatureInputWithRes> extends FeatureConvertRes<T> {

    // START BEAN PROPERTIES

    /** Direction of the distance being converted, defaults to a unit vector along the X-axis */
    @BeanField @Getter @Setter
    private DirectionVectorBean directionVector = new DirectionVectorBean(1.0, 0, 0);
    // END BEAN PROPERTIES

    private DirectionVector dv;

    public ConvertToPhysicalDistance(
            Feature<T> feature, UnitSuffix unitType, DirectionVector directionVector) {
        super(feature, unitType);
        this.directionVector = new DirectionVectorBean(directionVector);
    }

    @Override
    protected void beforeCalc(FeatureInitParams paramsInit) throws InitException {
        super.beforeCalc(paramsInit);
        dv = directionVector.createVector();
    }

    @Override
    protected double convertToPhysical(double value, ImageResolution res)
            throws FeatureCalcException {
        // We use arbitrary direction as everything should be the same in a isometric XY plane
        return ImageUnitConverter.convertToPhysicalDistance(value, res, dv);
    }
}
