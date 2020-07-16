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
/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.bbox;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@RequiredArgsConstructor
public class ExtractedObjectGenerator extends RasterGenerator
        implements IterableObjectGenerator<ObjectMask, Stack> {

    // START REQUIRED ARGUMENTS
    private final DrawObjectsGenerator rgbObectGenerator;
    private final IterableObjectGenerator<BoundingBox, Stack> chnlGenerator;
    private final String manifestFunction;
    private final boolean mip;
    // END REQUIRED ARGUMENTS

    private ObjectMask element;

    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        try {
            chnlGenerator.setIterableElement(element.getBoundingBox());
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }

        Stack chnlExtracted = chnlGenerator.getGenerator().generate();

        if (mip) {
            chnlExtracted = chnlExtracted.maxIntensityProj();
        }

        // We apply the generator
        try {
            rgbObectGenerator.setBackground(Optional.of(DisplayStack.create(chnlExtracted)));
        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }

        ObjectMask object = this.getIterableElement();

        if (mip) {
            object = object.flattenZ();
        }

        // We create a version that is relative to the extracted section
        ObjectCollectionWithProperties objects =
                new ObjectCollectionWithProperties(
                        new ObjectMask(
                                new BoundingBox(object.getVoxelBox().extent()),
                                object.binaryVoxelBox()));
        rgbObectGenerator.setIterableElement(objects);

        return rgbObectGenerator.generate();
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
        return rgbObectGenerator.isRGB();
    }
}
