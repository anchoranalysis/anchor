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

import io.vavr.control.Either;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryUnsignedByte;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties; // NOSONAR
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributes;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.image.io.stack.output.generator.RasterGeneratorSelectFormat;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;

/**
 * A base class for generators that draw an {@link ObjectCollection} upon a {@link RGBStack}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class ObjectsAsRGBGenerator
        extends RasterGeneratorSelectFormat<ObjectCollectionWithProperties> {

    private static final ChannelFactorySingleType CHANNEL_FACTORY =
            new ChannelFactoryUnsignedByte();

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

            RGBStack backgroundRegion = generateBackgroundRegion(element, background);
            drawObject.drawCollection(generateMasks(element), backgroundRegion, attributes);
            return backgroundRegion.asStack();
        } catch (OperationFailedException | CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public StackWriteAttributes guaranteedImageAttributes() {
        return StackWriteAttributesFactory.rgb(isBackground2D(), false);
    }

    /**
     * Creates a {@link RGBStack} containing the background, without objects being drawn upon it.
     *
     * <p>This background may be all or only a region of the entire background-stack.
     *
     * @param objects the current objects to be drawn.
     * @param background the entire background.
     * @return a newly created {@link RGBStack}, as above.
     * @throws CreateException if the background cannot be successfully created.
     */
    protected abstract RGBStack generateBackgroundRegion(
            ObjectCollectionWithProperties objects, Either<Dimensions, DisplayStack> background)
            throws CreateException;

    /**
     * Creates a {@link ObjectCollectionWithProperties} indicative of the masks that will be imposed
     * on top of the background-region.
     *
     * @param objects the objects to draw.
     * @return either {@code objects} or a derived-representation of {@code objects}, as will be
     *     drawn on the background-region.
     * @throws CreateException if the background cannot be successfully created.
     */
    protected abstract ObjectCollectionWithProperties generateMasks(
            ObjectCollectionWithProperties objects) throws CreateException;

    /**
     * Creates an empty {@link RGBStack}.
     *
     * <p>Empty implies all voxels are zero-valued i.e. have black color.
     *
     * @param dimensions the size of the stack to create.
     * @return the newly created {@link RGBStack}.
     */
    protected static RGBStack createEmptyStackFor(Dimensions dimensions) {
        return new RGBStack(dimensions, CHANNEL_FACTORY);
    }

    /** Is the background a 2D image? */
    private boolean isBackground2D() {
        return background.fold(Function.identity(), DisplayStack::dimensions).z() == 1;
    }
}
