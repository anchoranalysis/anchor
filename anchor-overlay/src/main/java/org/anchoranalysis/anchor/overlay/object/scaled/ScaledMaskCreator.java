/* (C)2020 */
package org.anchoranalysis.anchor.overlay.object.scaled;

import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

/**
 * Creates a scaled version of a mask from a mark/object that is not scaled
 *
 * @author Owen Feehan
 */
public interface ScaledMaskCreator {

    /**
     * Creates a scaled-version of the mask
     *
     * @param overlayWriter what writes an overlay onto a raster
     * @param omUnscaled unscaled object-mask
     * @param scaleFactor how much to scale by (e.g. 0.5 scales the X dimension to 50%)
     * @param originalObject the object from which omUnscaled was derived
     * @param sdScaled the scene-dimensions when scaled to match scaleFactor
     * @param bv binary-values for creating the mask
     * @return the scaled object-mask
     * @throws CreateException
     */
    ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            ObjectWithProperties omUnscaled,
            double scaleFactor,
            Object originalObject,
            ImageDimensions sdScaled,
            BinaryValuesByte bv)
            throws CreateException;
}
