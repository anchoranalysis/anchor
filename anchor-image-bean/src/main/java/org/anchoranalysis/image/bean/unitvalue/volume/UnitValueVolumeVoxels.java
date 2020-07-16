/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.volume;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.extent.ImageResolution;

@NoArgsConstructor
@AllArgsConstructor
public class UnitValueVolumeVoxels extends UnitValueVolume {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double value;
    // END BEAN PROPERTIES

    @Override
    public double resolveToVoxels(Optional<ImageResolution> resolution) {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%.2f", value);
    }
}
