/* (C)2020 */
package org.anchoranalysis.image.feature.bean.physical.convert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.feature.bean.physical.FeatureSingleElemWithRes;

@NoArgsConstructor
public abstract class FeatureConvertRes<T extends FeatureInputWithRes>
        extends FeatureSingleElemWithRes<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String unitType;
    // END BEAN PROPERTIES

    public FeatureConvertRes(Feature<T> feature, UnitSuffix unitType) {
        super(feature);
        this.unitType = SpatialConversionUtilities.unitMeterStringDisplay(unitType);
    }

    @Override
    protected double calcWithRes(double value, ImageResolution res) throws FeatureCalcException {
        double valuePhysical = convertToPhysical(value, res);
        return convertToUnits(valuePhysical);
    }

    protected abstract double convertToPhysical(double value, ImageResolution res)
            throws FeatureCalcException;

    private double convertToUnits(double valuePhysical) {
        SpatialConversionUtilities.UnitSuffix prefixType =
                SpatialConversionUtilities.suffixFromMeterString(unitType);
        return SpatialConversionUtilities.convertToUnits(valuePhysical, prefixType);
    }
}
