/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.area;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.bean.nonbean.error.UnitValueException;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageResolution;

/**
 * Area expressed in physical coordinates
 *
 * @author Owen Feehan
 */
public class UnitValueAreaPhysical extends UnitValueArea {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String prefix = "";
    // END BEAN PROPERTIES

    @Override
    public double resolveToVoxels(Optional<ImageResolution> resolution) throws UnitValueException {

        if (!resolution.isPresent()) {
            throw new UnitValueException(
                    "An image resolution is required to calculate physical-area but it is missing");
        }

        UnitSuffix unitPrefix = SpatialConversionUtilities.suffixFromMeterString(prefix);

        double valueAsBase = SpatialConversionUtilities.convertFromUnits(getValue(), unitPrefix);

        return ImageUnitConverter.convertFromPhysicalArea(valueAsBase, resolution.get());
    }

    @Override
    public String toString() {
        if (prefix != null && !prefix.isEmpty()) {
            return String.format("%.2f%s", getValue(), prefix);
        } else {
            return String.format("%.2f", getValue());
        }
    }
}
