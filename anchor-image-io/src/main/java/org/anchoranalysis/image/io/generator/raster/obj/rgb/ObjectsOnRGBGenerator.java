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
package org.anchoranalysis.image.io.generator.raster.obj.rgb;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * A base class for generators that draw objects on top of a RGB-Stack
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public abstract class ObjectsOnRGBGenerator extends RasterGenerator
        implements IterableObjectGenerator<ObjectCollectionWithProperties, Stack> {

    private static final ManifestDescription MANIFEST_DESCRIPTION =
            new ManifestDescription("raster", "rgbObjects");

    // START REQUIRED ARGUMENTS
    private final DrawObject drawObject;
    private final ObjectDrawAttributes attributes;

    @Getter @Setter private Optional<DisplayStack> background;
    // END REQUIRED ARGUMENTS

    // Iterable element
    private ObjectCollectionWithProperties element;

    public ObjectsOnRGBGenerator(
            DrawObject drawObject,
            ObjectDrawAttributes attributes,
            Optional<DisplayStack> background) {
        super();
        this.drawObject = drawObject;
        this.attributes = attributes;
        this.background = background;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {
        try {
            if (!background.isPresent()) {
                throw new OutputWriteFailedException(
                        "A background is required for this generator, but has not been set.");
            }

            RGBStack backgroundRGB = generateBackground(background.get());
            drawObject.write(generateMasks(), backgroundRGB, attributes);
            return backgroundRGB.asStack();
        } catch (OperationFailedException | CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public ObjectCollectionWithProperties getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(ObjectCollectionWithProperties element) {
        this.element = element;
    }

    @Override
    public ObjectGenerator<Stack> getGenerator() {
        return this;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        // NOTHING TO DO
    }

    @Override
    public void end() throws OutputWriteFailedException {
        // NOTHING TO DO
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(MANIFEST_DESCRIPTION);
    }

    protected abstract RGBStack generateBackground(DisplayStack background) throws CreateException;

    protected abstract ObjectCollectionWithProperties generateMasks() throws CreateException;
}
