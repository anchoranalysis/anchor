/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.distance;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.DirectionVector;

// Measures either area or volume (depending if the do3D flag is employed)
public class UnitValueDistancePhysical extends UnitValueDistance {

    /** */
    private static final long serialVersionUID = 1L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double value;

    @BeanField @AllowEmpty @Getter @Setter private String unitType;
    // END BEAN PROPERTIES

    @Override
    public double resolve(Optional<ImageResolution> res, DirectionVector dirVector)
            throws OperationFailedException {

        if (!res.isPresent()) {
            throw new OperationFailedException(
                    "An image-resolution is missing, so cannot calculate physical distances");
        }

        UnitSuffix unitPrefix = SpatialConversionUtilities.suffixFromMeterString(unitType);

        double valueAsBase = SpatialConversionUtilities.convertFromUnits(value, unitPrefix);

        return ImageUnitConverter.convertFromPhysicalDistance(valueAsBase, res.get(), dirVector);
    }
}
