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
 * Draws the outline of each object-mask.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class Outline extends DrawObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int outlineWidth;

    @BeanField @Getter @Setter private boolean force2D = false;
    // END BEAN PROPERTIES

    public Outline() {
        this(1);
    }

    public Outline(int outlineWidth) {
        this(outlineWidth, false);
    }

    @Override
    public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
            throws CreateException {

        ObjectMask object =
                FindOutline.outline(
                        mask.getMask(), outlineWidth, true, (dim.getZ() > 1) && !force2D);

        ObjectWithProperties objectWithProperties =
                new ObjectWithProperties(object, mask.getProperties());

        return new PrecalcOverlay(mask) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                assert (object.getVoxelBox().extent().getZ() > 0);
                // TODO this can get broken! Fix!
                assert (object.getBoundingBox().cornerMin().getZ() >= 0);

                IntersectionWriter.writeRGBMaskIntersection(
                        object,
                        attributes.colorFor(objectWithProperties, iteration),
                        background,
                        restrictTo);
            }
        };
    }
}
