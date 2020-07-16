/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.distance;

import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.DirectionVector;

@NoArgsConstructor
public class UnitValueDistanceVoxels extends UnitValueDistance {

    /** */
    private static final long serialVersionUID = 1L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double value;
    // END BEAN PROPERTIES

    public UnitValueDistanceVoxels(double value) {
        this.value = value;
    }

    @Override
    public double resolve(Optional<ImageResolution> res, DirectionVector dirVector) {
        return value;
    }
}
