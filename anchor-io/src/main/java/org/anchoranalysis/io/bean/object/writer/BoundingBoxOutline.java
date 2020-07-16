/* (C)2020 */
package org.anchoranalysis.io.bean.object.writer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.outline.FindOutline;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Draws the outline of the bounding-box for each object.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class BoundingBoxOutline extends DrawObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int outlineWidth;
    // END BEAN PROPERTIES

    public BoundingBoxOutline() {
        this(1);
    }

    @Override
    public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
            throws CreateException {
        ObjectMask outline =
                FindOutline.outline(
                        createBoundingBoxObject(mask.getMask()),
                        outlineWidth,
                        true,
                        dim.getZ() > 1);

        return new PrecalcOverlay(mask) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                IntersectionWriter.writeRGBMaskIntersection(
                        outline, attributes.colorFor(mask, iteration), background, restrictTo);
            }
        };
    }

    private ObjectMask createBoundingBoxObject(ObjectMask mask) {
        ObjectMask bbox = mask.duplicate();
        bbox.getVoxelBox().setAllPixelsTo(1);
        return bbox;
    }
}
