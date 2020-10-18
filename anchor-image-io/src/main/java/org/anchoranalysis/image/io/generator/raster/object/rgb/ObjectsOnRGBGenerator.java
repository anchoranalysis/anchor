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

package org.anchoranalysis.image.io.generator.raster.object.rgb;

import io.vavr.control.Either;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.rgb.RGBStack;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;

/**
 * A base class for generators that draw objects on top of a RGB-Stack
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class ObjectsOnRGBGenerator
        extends RasterGenerator<ObjectCollectionWithProperties> {

    private static final ChannelFactorySingleType CHANNEL_FACTORY = new ChannelFactoryByte();

    private static final ManifestDescription MANIFEST_DESCRIPTION =
            new ManifestDescription("raster", "rgbObjects");

    // START REQUIRED ARGUMENTS
    /** Determines how an object is drawn (on the background). */
    private final DrawObject drawObject;

    /** An association of color and/or other identifies with each object. */
    private final ObjectDrawAttributes attributes;

    /** A background image or dimensions to define an empty background. */
    @Getter @Setter private Either<Dimensions, DisplayStack> background;
    // END REQUIRED ARGUMENTS

    @Override
    public Stack transform(ObjectCollectionWithProperties element)
            throws OutputWriteFailedException {
        try {
            if (background == null) {
                throw new OutputWriteFailedException(
                        "No background has been set, as is needed by this generator");
            }

            RGBStack backgroundRGB = generateBackground(element, background);
            drawObject.write(generateMasks(element), backgroundRGB, attributes);
            return backgroundRGB.asStack();
        } catch (OperationFailedException | CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(MANIFEST_DESCRIPTION);
    }

    @Override
    public StackWriteOptions writeOptions() {
        return StackWriteOptions.rgb(isAlways2D());
    }

    protected abstract RGBStack generateBackground(
            ObjectCollectionWithProperties element, Either<Dimensions, DisplayStack> background)
            throws CreateException;

    protected abstract ObjectCollectionWithProperties generateMasks(
            ObjectCollectionWithProperties element) throws CreateException;

    protected static RGBStack createEmptyStackFor(Dimensions dimensions) {
        return new RGBStack(dimensions, CHANNEL_FACTORY);
    }

    private boolean isAlways2D() {
        return background.fold(Function.identity(), DisplayStack::dimensions).z() == 1;
    }
}
