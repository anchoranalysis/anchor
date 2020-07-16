/* (C)2020 */
package org.anchoranalysis.anchor.overlay.writer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Overlays with additional pre-calculations that make them quicker to draw onto a RGBStack
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class PrecalcOverlay {

    @Getter private final ObjectWithProperties first;

    public abstract void writePrecalculatedMask(
            RGBStack background,
            ObjectDrawAttributes attributes,
            int iteration,
            BoundingBox restrictTo)
            throws OperationFailedException;
}
