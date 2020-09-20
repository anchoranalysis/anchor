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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorWithElement;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;

/**
 * A base class for generators that draw objects on top of a RGB-Stack
 *
 * @author Owen Feehan
 */
public abstract class ObjectsOnRGBGenerator extends RasterGeneratorWithElement<ObjectCollectionWithProperties> {

    private static final ChannelFactorySingleType CHANNEL_FACTORY = new ChannelFactoryByte();

    private static final ManifestDescription MANIFEST_DESCRIPTION =
            new ManifestDescription("raster", "rgbObjects");

    // START REQUIRED ARGUMENTS
    private final DrawObject drawObject;
    private final ObjectDrawAttributes attributes;

    @Getter @Setter private Either<Dimensions, DisplayStack> background;
    // END REQUIRED ARGUMENTS

    public ObjectsOnRGBGenerator(
            DrawObject drawObject,
            ObjectDrawAttributes attributes,
            Either<Dimensions, DisplayStack> background) {
        super();
        this.drawObject = drawObject;
        this.attributes = attributes;
        this.background = background;
    }

    @Override
    public Stack transform() throws OutputWriteFailedException {
        try {
            if (background == null) {
                throw new OutputWriteFailedException(
                        "No background has been set, as is needed by this generator");
            }

            RGBStack backgroundRGB = generateBackground(background);
            drawObject.write(generateMasks(), backgroundRGB, attributes);
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
    public RasterWriteOptions rasterWriteOptions() {
        return RasterWriteOptions.rgbMaybe3D();
    }

    protected abstract RGBStack generateBackground(Either<Dimensions, DisplayStack> background)
            throws CreateException;

    protected abstract ObjectCollectionWithProperties generateMasks() throws CreateException;

    protected static RGBStack createEmptyStackFor(Dimensions dimensions) {
        return new RGBStack(dimensions, CHANNEL_FACTORY);
    }
}
