/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.io.object.output.rgb;

import com.google.common.base.Functions;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.bean.spatial.Padding;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.merge.ObjectMaskMerger;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Similar to {@link DrawObjectsGenerator}
 *
 * <p>BUT with the background stack cropped to contain the objects a small margin
 *
 * @author Owen Feehan
 */
public class DrawCroppedObjectsGenerator extends ObjectsAsRGBGenerator {

    @Getter @Setter private Padding padding;

    private BoundingBox box;

    public DrawCroppedObjectsGenerator(
            DrawObject drawObject, DisplayStack background, ColorIndex colorIndex) {
        super(drawObject, new ObjectDrawAttributes(colorIndex), Either.right(background));
    }

    @Override
    protected RGBStack generateBackground(
            ObjectCollectionWithProperties element, Either<Dimensions, DisplayStack> background)
            throws CreateException {
        Extent extent = background.fold(Functions.identity(), DisplayStack::dimensions).extent();

        ObjectCollection objects = element.withoutProperties();

        if (objects.isEmpty()) {
            throw new CreateException("This generator expects at least one object to be present");
        }

        // Get a bounding box that contains all the objects
        this.box = ObjectMaskMerger.mergeBoundingBoxes(objects.streamStandardJava());

        box = growBBBox(box, extent);

        // Extract the relevant piece of background
        return background.fold(
                dimensions -> createEmptyStackFor(new Dimensions(box.extent())),
                stack -> ConvertDisplayStackToRGB.convertCropped(stack, box));
    }

    @Override
    protected ObjectCollectionWithProperties generateMasks(ObjectCollectionWithProperties element)
            throws CreateException {
        // Create a new set of object-masks, relative to the box position
        return relativeTo(element.withoutProperties(), box);
    }

    private BoundingBox growBBBox(BoundingBox box, Extent containingExtent) {
        if (padding.noPadding()) {
            return box;
        }

        return box.growBy(padding.asPoint(), containingExtent);
    }

    private static ObjectCollectionWithProperties relativeTo(
            ObjectCollection objects, BoundingBox src) {

        ObjectCollectionWithProperties out = new ObjectCollectionWithProperties();

        for (ObjectMask objectMask : objects) {
            BoundingBox boxNew =
                    new BoundingBox(
                            objectMask.boundingBox().relativePositionTo(src),
                            objectMask.boundingBox().extent());
            out.add(
                    new ObjectMask(
                            boxNew, objectMask.binaryVoxels().voxels(), objectMask.binaryValues()));
        }

        return out;
    }
}
