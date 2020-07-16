/* (C)2020 */
package org.anchoranalysis.mpp.io.cfg.generator;

import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Converts a configuration to a set of object-masks, using a simple {@link DrawObject} for all
 * objects.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class SimpleOverlayWriter extends DrawOverlay {

    private final DrawObject drawObject;

    @Override
    public void writePrecalculatedOverlays(
            List<PrecalcOverlay> precalculatedMasks,
            ImageDimensions dimensions,
            RGBStack background,
            ObjectDrawAttributes attributes,
            BoundingBox restrictTo)
            throws OperationFailedException {

        for (int i = 0; i < precalculatedMasks.size(); i++) {
            precalculatedMasks.get(i).writePrecalculatedMask(background, attributes, i, restrictTo);
        }
    }

    @Override
    public void writeOverlaysIfIntersects(
            ColoredOverlayCollection overlays,
            RGBStack stack,
            IDGetter<Overlay> idGetter,
            List<BoundingBox> intersectList)
            throws OperationFailedException {

        writeOverlays(
                overlays.subsetWhereBBoxIntersects(stack.getDimensions(), this, intersectList),
                stack,
                idGetter);
    }

    @Override
    public DrawObject getDrawObject() {
        return drawObject;
    }
}
