/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.obj.rgb;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ops.ObjectMaskMerger;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Similar to {@link DrawObjectsGenerator}
 *
 * <p>BUT with the background stack cropped to contain the objects a small margin
 *
 * @author Owen Feehan
 */
public class DrawCroppedObjectsGenerator extends ObjectsOnRGBGenerator {

    @Getter @Setter private int paddingXY = 0;

    @Getter @Setter private int paddingZ = 0;

    private BoundingBox bbox;

    public DrawCroppedObjectsGenerator(
            DrawObject drawObject, DisplayStack background, ColorIndex colorIndex) {
        super(drawObject, new ObjectDrawAttributes(colorIndex), Optional.of(background));
    }

    @Override
    protected RGBStack generateBackground(DisplayStack background) throws CreateException {
        try {
            ObjectCollection objects = getIterableElement().withoutProperties();

            if (objects.isEmpty()) {
                throw new CreateException("This generator expects at least one mask to be present");
            }

            // Get a bounding box that contains all the objects
            this.bbox = ObjectMaskMerger.mergeBoundingBoxes(objects);

            bbox = growBBBox(bbox, background.getDimensions().getExtent());

            // Extract the relevant piece of background
            return ConvertDisplayStackToRGB.convertCropped(background, bbox);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    protected ObjectCollectionWithProperties generateMasks() throws CreateException {
        // Create a new set of object masks, relative to the bbox position
        return relTo(getIterableElement().withoutProperties(), bbox);
    }

    private BoundingBox growBBBox(BoundingBox bbox, Extent containingExtent) {
        assert (paddingXY >= 0);
        assert (paddingZ >= 0);

        if (paddingXY == 0 && paddingZ == 0) {
            return bbox;
        }

        return bbox.growBy(new Point3i(paddingXY, paddingXY, paddingZ), containingExtent);
    }

    private static ObjectCollectionWithProperties relTo(ObjectCollection objects, BoundingBox src) {

        ObjectCollectionWithProperties out = new ObjectCollectionWithProperties();

        for (ObjectMask objectMask : objects) {
            BoundingBox bboxNew =
                    new BoundingBox(
                            objectMask.getBoundingBox().relPosTo(src),
                            objectMask.getBoundingBox().extent());
            out.add(
                    new ObjectMask(
                            bboxNew,
                            objectMask.binaryVoxelBox().getVoxelBox(),
                            objectMask.getBinaryValues()));
        }

        return out;
    }
}
