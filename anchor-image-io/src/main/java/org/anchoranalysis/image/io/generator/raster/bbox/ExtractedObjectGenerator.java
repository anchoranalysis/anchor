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

package org.anchoranalysis.image.io.generator.raster.bbox;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.DrawObjectsGenerator;
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
 * Creates images of an object drawn on a background only in a local region around its bounding box.
 * 
 * <p>This provides a visualization of an object and a small around of immediate background context.
 * 
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class ExtractedObjectGenerator extends RasterGenerator
        implements IterableObjectGenerator<ObjectMask, Stack> {

    // START REQUIRED ARGUMENTS
    private final DrawObjectsGenerator drawObjectsGenerator;
    private final IterableObjectGenerator<BoundingBox, Stack> backgroundGenerator;
    private final String manifestFunction;
    private final boolean mip;
    // END REQUIRED ARGUMENTS

    private ObjectMask element;

    /**
     * Creates an extracted-object generator that draws an outline
     * 
     * @param backgroundGenerator
     * @param outlineWidth
     * @param colorIndex
     * @param mip
     * @param manifestFunction
     */
    public ExtractedObjectGenerator(
        IterableObjectGenerator<BoundingBox, Stack> backgroundGenerator,
        int outlineWidth,
        ColorIndex colorIndex,
        boolean mip,
        String manifestFunction
    ) {
        this.drawObjectsGenerator = new DrawObjectsGenerator(new Outline(outlineWidth), colorIndex);
        this.backgroundGenerator = backgroundGenerator;
        this.mip = mip;
        this.manifestFunction = manifestFunction;
    }
    
    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        try {
            backgroundGenerator.setIterableElement(element.getBoundingBox());
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }

        Stack channelExtracted = backgroundGenerator.getGenerator().generate();

        if (mip) {
            channelExtracted = channelExtracted.maximumIntensityProjection();
        }

        // Apply the generator
        drawObjectsGenerator.setBackground( createBackground(channelExtracted) );

        ObjectMask object = this.getIterableElement();

        if (mip) {
            object = object.flattenZ();
        }

        // An object-mask that is relative to the extracted section
        drawObjectsGenerator.setIterableElement( new ObjectCollectionWithProperties(changeBoundingBox(object)) );

        return drawObjectsGenerator.generate();
    }
    
    private Optional<DisplayStack> createBackground(Stack channelExtracted) throws OutputWriteFailedException {
        try {
            return Optional.of(DisplayStack.create(channelExtracted));
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public ObjectMask getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(ObjectMask element) {
        this.element = element;
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", manifestFunction));
    }

    @Override
    public boolean isRGB() {
        return drawObjectsGenerator.isRGB();
    }
    
    /** Changes the bounding-box to match the object */
    private ObjectMask changeBoundingBox(ObjectMask object) {
        return new ObjectMask(
                        new BoundingBox(object.getVoxelBox().extent()),
                        object.binaryVoxelBox());
    }
}
