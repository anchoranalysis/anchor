/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.areavolume;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.bean.nonbean.error.UnitValueException;
import org.anchoranalysis.image.extent.ImageResolution;

public abstract class UnitValueAreaOrVolume extends AnchorBean<UnitValueAreaOrVolume> {

    /**
     * Resolves a measurement of area/volume (in whatever units) to units corresponding to the image
     * pixels/voxels.
     *
     * @param resolution
     * @return the resolved-value (pixels for area, voxels for volume).
     * @throws UnitValueException
     */
    public abstract double resolveToVoxels(Optional<ImageResolution> resolution)
            throws UnitValueException;
}
