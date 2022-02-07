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
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Similar to {@link DrawObjectsGenerator}, but with the background stack cropped to focus only on
 * the region containing objects.
 *
 * <p>Padding is also placed around the objects.
 *
 * @author Owen Feehan
 */
public class DrawCroppedObjectsGenerator extends ObjectsAsRGBGenerator {

    // START REQUIRED ARGUMENTS
    /**
     * How much padding to place at the margin.
     *
     * <p>This is a fixed-size space at the margins that is guaranteed to contain no objects.
     */
    private final Padding padding;
    // END REQUIRED ARGUMENTS

    /**
     * Identifies which region in {@code background} is extracted for the final image.
     *
     * <p>null when not yet created.
     */
    private BoundingBox box;

    /**
     * Create with a particular background and method for drawing-objects.
     *
     * @param drawObject method for drawing an object on an image.
     * @param background the background image, on which objects are drawn.
     * @param padding padding used to add a margin around the objects in the cropped area.
     * @param colors the colors to use for objects, indexed by a particular identifier.
     */
    public DrawCroppedObjectsGenerator(
            DrawObject drawObject, DisplayStack background, Padding padding, ColorIndex colors) {
        super(drawObject, new ObjectDrawAttributes(colors), Either.right(background));
        this.padding = padding;
    }

    @Override
    protected RGBStack generateBackgroundRegion(
            ObjectCollectionWithProperties objects, Either<Dimensions, DisplayStack> background)
            throws CreateException {
        Extent extent = background.fold(Functions.identity(), DisplayStack::dimensions).extent();

        if (objects.isEmpty()) {
            throw new CreateException("This generator expects at least one object to be present");
        }

        // Get a bounding box that contains all the objects
        this.box =
                ObjectMaskMerger.mergeBoundingBoxes(
                        objects.withoutProperties().streamStandardJava());

        box = calculateBoxIncludingPadding(box, extent);

        // Extract the relevant piece of background
        return background.fold(
                dimensions -> createEmptyStackFor(new Dimensions(box.extent())),
                stack -> ConvertDisplayStackToRGB.convertCropped(stack, box));
    }

    @Override
    protected ObjectCollectionWithProperties generateMasks(ObjectCollectionWithProperties objects)
            throws CreateException {
        // Create a new set of object-masks, relative to the box position
        return relativeTo(objects.withoutProperties(), box);
    }

    /** Determine the bounding-box, including padding, to extract from the image. */
    private BoundingBox calculateBoxIncludingPadding(BoundingBox box, Extent containingExtent) {
        if (padding.hasNoPadding()) {
            return box;
        } else {
            return box.growBy(padding.asPoint(), containingExtent);
        }
    }

    /** Express {@code objects} coordinates, as relative-coordinates to {@code source}. */
    private static ObjectCollectionWithProperties relativeTo(
            ObjectCollection objects, BoundingBox source) {

        ObjectCollectionWithProperties out = new ObjectCollectionWithProperties(objects.size());

        for (ObjectMask objectMask : objects) {
            BoundingBox boxNew =
                    BoundingBox.createReuse(
                            objectMask.boundingBox().relativePositionTo(source),
                            objectMask.boundingBox().extent());
            out.add(
                    new ObjectMask(
                            boxNew, objectMask.binaryVoxels().voxels(), objectMask.binaryValues()));
        }

        return out;
    }
}
