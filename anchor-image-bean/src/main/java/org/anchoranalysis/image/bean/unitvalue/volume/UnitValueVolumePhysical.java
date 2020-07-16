/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.volume;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.bean.nonbean.error.UnitValueException;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageResolution;

// Measures either area or volume (depending if the do3D flag is employed)
public class UnitValueVolumePhysical extends UnitValueVolume {

    // START VALUE
    @BeanField @Getter @Setter private double value; // value in metres

    @BeanField @Getter @Setter private String unitType = "";
    // END VALUE

    @Override
    public double resolveToVoxels(Optional<ImageResolution> resolution) throws UnitValueException {
        if (!resolution.isPresent()) {
            throw new UnitValueException(
                    "An image resolution is required to calculate physical-volume but it is missing");
        }

        UnitSuffix unitPrefix = SpatialConversionUtilities.suffixFromMeterString(unitType);

        double valueAsBase = SpatialConversionUtilities.convertFromUnits(value, unitPrefix);

        return ImageUnitConverter.convertFromPhysicalVolume(valueAsBase, resolution.get());
    }

    @Override
    public String toString() {
        if (unitType != null && !unitType.isEmpty()) {
            return String.format("%.2f%s", value, unitType);
        } else {
            return String.format("%.2f", value);
        }
    }
}
