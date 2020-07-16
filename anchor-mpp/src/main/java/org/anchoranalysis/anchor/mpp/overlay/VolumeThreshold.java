/* (C)2020 */
package org.anchoranalysis.anchor.mpp.overlay;

import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.object.scaled.ScaledMaskCreator;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

@AllArgsConstructor
class VolumeThreshold implements ScaledMaskCreator {

    private ScaledMaskCreator greaterThanEqualThreshold;
    private ScaledMaskCreator lessThreshold;
    private int threshold;

    @Override
    public ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            ObjectWithProperties omUnscaled,
            double scaleFactor,
            Object originalObject,
            ImageDimensions sdScaled,
            BinaryValuesByte bvOut)
            throws CreateException {

        Mark originalMark = (Mark) originalObject;

        // TODO using region 0, fix
        double zoomVolume = originalMark.volume(0) * Math.pow(scaleFactor, 2);

        if (zoomVolume > threshold) {
            return greaterThanEqualThreshold.createScaledMask(
                    overlayWriter, omUnscaled, scaleFactor, originalObject, sdScaled, bvOut);
        } else {
            return lessThreshold.createScaledMask(
                    overlayWriter, omUnscaled, scaleFactor, originalObject, sdScaled, bvOut);
        }
    }
}
