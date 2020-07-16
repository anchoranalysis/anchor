/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.area;

import java.util.Optional;
import org.anchoranalysis.image.extent.ImageResolution;

/**
 * Area expressed as square pixels
 *
 * @author Owen Feehan
 */
public class UnitValueAreaPixels extends UnitValueArea {

    public UnitValueAreaPixels() {
        // Standard bean constructor
    }

    public UnitValueAreaPixels(double value) {
        super(value);
    }

    @Override
    public double resolveToVoxels(Optional<ImageResolution> resolution) {
        return getValue();
    }

    @Override
    public String toString() {
        return String.format("%.2f", getValue());
    }
}
