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

package org.anchoranalysis.image.io.generator.raster.boundingbox;

import io.vavr.control.Either;
import java.util.Optional;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Creates images of object(s) drawn on a background only in a local region around their bounding box.
 *
 * <p>This provides a visualization of an object(s) and a small around of immediate background context.
 *
 * @author Owen Feehan
 */
public class DrawObjectOnStackGenerator extends RasterGenerator
        implements IterableObjectGenerator<ObjectsWithBoundingBox, Stack> {

    private static final ManifestDescription MANIFEST_DESCRIPTION =
            new ManifestDescription("raster", "extractedObjectOutline");

    // START REQUIRED ARGUMENTS
    private final DrawObjectsGenerator drawObjectsGenerator;
    private final Optional<IterableObjectGenerator<BoundingBox, Stack>> backgroundGenerator;
    private final boolean flatten;
    // END REQUIRED ARGUMENTS

    private ObjectsWithBoundingBox element;

    /**
     * Creates the generator with a stack as the background - and with default color green and
     * flattened in Z
     *
     * @param background stack that exists as a background for the object
     * @param colors colors to use for outling of objects
     * @param outlineWidth width of the outline around an object
     */
    public static DrawObjectOnStackGenerator createFromStack(Stack background, int outlineWidth, ColorList colors) {
        return createFromGenerator(
                new ExtractBoundingBoxAreaFromStackGenerator(background), outlineWidth, colors);
    }

    /**
     * Creates the generator with an empty background - and with default color green and flattened
     * in Z
     *
     * @param outlineWidth width of the outline around an object
     * @param colors colors to use for outling of objects
     * @return
     */
    public static DrawObjectOnStackGenerator createWithEmptyBackground(int outlineWidth, ColorList colors) {
        return new DrawObjectOnStackGenerator(Optional.empty(), outlineWidth, colors, true);
    }

    /**
     * Creates the generator with maybe a stack as the background, or else an empty background - and
     * with default color green and flattened in Z
     *
     * @param background stack that exists as a background for the object (or none, in which case a
     *     0-valued grayscale background is assumed)
     * @param colors colors to use for outling of objects
     * @param outlineWidth width of the outline around an object
     */
    public static DrawObjectOnStackGenerator createFromStack(
            Optional<Stack> background, int outlineWidth, ColorList colors) {
        if (background.isPresent()) {
            return createFromStack(background.get(), outlineWidth, colors);
        } else {
            return createWithEmptyBackground(outlineWidth, colors);
        }
    }

    /**
     * Creates an extracted-object generator that draws an outline - with default color green and
     * flattened in Z
     *
     * @param backgroundGenerator generates a background for a bounding-box
     * @param outlineWidth width of the outline around an object
     * @param colors colors to use for outling of objects
     */
    public static DrawObjectOnStackGenerator createFromGenerator(
            IterableObjectGenerator<BoundingBox, Stack> backgroundGenerator, int outlineWidth, ColorList colors) {
        return new DrawObjectOnStackGenerator(
                Optional.of(backgroundGenerator), outlineWidth, colors, true);
    }

    /**
     * Creates an extracted-object generator that draws an outline
     *
     * @param backgroundGenerator generates a background for a bounding-box, otherwise the
     *     background is set to all zeros
     * @param outlineWidth width of the outline around an object
     * @param colorIndex the colors used by successive objects in rotation
     * @param flatten whether to flatten in the z-dimension (maximum-intensity projection of stack
     *     and bounding-box)
     */
    private DrawObjectOnStackGenerator(
            Optional<IterableObjectGenerator<BoundingBox, Stack>> backgroundGenerator,
            int outlineWidth,
            ColorIndex colorIndex,
            boolean flatten) {
        this.drawObjectsGenerator = new DrawObjectsGenerator(new Outline(outlineWidth), colorIndex);
        this.backgroundGenerator = backgroundGenerator;
        this.flatten = flatten;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        // Apply the generator
        drawObjectsGenerator.setBackground(createBackground());

        ObjectsWithBoundingBox objects = this.getIterableElement();

        ObjectCollection objectsForDrawing = objects.objects().stream().map(object->
            prepareObjectForDrawing(object, objects.boundingBox()) );
        
        // An object-mask that is relative to the extracted section
        drawObjectsGenerator.setIterableElement(
                new ObjectCollectionWithProperties(objectsForDrawing) );

        return drawObjectsGenerator.generate();
    }

    private Either<ImageDimensions, DisplayStack> createBackground()
            throws OutputWriteFailedException {

        if (!backgroundGenerator.isPresent()) {
            // Exit early if there's no background to be extracted
            return Either.left(new ImageDimensions(element.boundingBox().extent()));
        }

        try {
            backgroundGenerator.get().setIterableElement(element.boundingBox());
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }

        Stack channelExtracted = backgroundGenerator.get().getGenerator().generate();

        if (flatten) {
            channelExtracted = channelExtracted.maximumIntensityProjection();
        }

        try {
            return Either.right(DisplayStack.create(channelExtracted));
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public ObjectsWithBoundingBox getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(ObjectsWithBoundingBox element) {
        this.element = element;
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(MANIFEST_DESCRIPTION);
    }

    @Override
    public boolean isRGB() {
        return drawObjectsGenerator.isRGB();
    }

    private ObjectMask prepareObjectForDrawing(ObjectMask object, BoundingBox containingBox) {
        if (flatten) {
            return relativeBoundingBoxToScene(object.flattenZ(), containingBox);
        } else {
            return relativeBoundingBoxToScene(object, containingBox);
        }
    }
    
    /** Changes the bounding-box to match the object rather than the global scene */
    private ObjectMask relativeBoundingBoxToScene(ObjectMask object, BoundingBox containingBox) {
        Point3i relativePosition = object.boundingBox().relPosTo(containingBox);
        return object.mapBoundingBoxPreserveExtent( boundingBox->boundingBox.shiftTo(relativePosition) );
    }
}