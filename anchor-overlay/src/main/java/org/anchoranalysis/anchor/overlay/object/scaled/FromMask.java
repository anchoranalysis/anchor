/* (C)2020 */
package org.anchoranalysis.anchor.overlay.object.scaled;

import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.scale.ScaleFactor;

public class FromMask implements ScaledMaskCreator {

    private static Interpolator interpolator = InterpolatorFactory.getInstance().binaryResizing();

    @Override
    public ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            ObjectWithProperties unscaled,
            double scaleFactor,
            Object originalObject,
            ImageDimensions sdScaled,
            BinaryValuesByte bvOut)
            throws CreateException {

        try {
            // Then we have to create the scaled-object fresh
            // We store it for next-time
            ObjectMask scaled =
                    unscaled.getMask().scaleNew(new ScaleFactor(scaleFactor), interpolator);

            assert (scaled.hasPixelsGreaterThan(0));

            // We keep the properties the same
            return new ObjectWithProperties(scaled, unscaled.getProperties());

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
