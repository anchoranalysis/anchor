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

import java.util.Optional;
import javax.annotation.Nullable;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.spatial.extent.Extent;
import org.anchoranalysis.spatial.extent.box.BoundingBox;
import org.anchoranalysis.spatial.extent.scale.ScaleFactor;

/**
 * A stack that can be used as a background (and maybe scaled)
 *
 * <p>In situations, where only particular regions of a large stack (or large after upscaling) are
 * desired, this provides a more efficient lazy interpolation strategy (just for the needed regions)
 * rather than eagerly interpolating the entire background.
 *
 * <p>It is always flattened via a maximum intensity projection.
 *
 * @author Owen Feehan
 */
public class ScaleableBackground {

    // START REQUIRED ARGUMENTS
    /** Stack to extract bounding-box regions form */
    private final Stack stack;

    /**
     * If defined, the stack is scaled (and interpolated) by this factor before a bounding box is
     * extracted
     */
    private final Optional<ScaleFactor> scaleFactor;

    /** Interpolator to use for scaling stacks, if necessary */
    @Nullable private final Interpolator interpolator;
    // END REQUIRED ARGUMENTS

    /**
     * Creates a background from a stack without any scaling
     *
     * @param stack the stack
     * @return a newly created class
     */
    public static ScaleableBackground noScaling(Stack stack) {
        // The interpolator is never used, so we can safely pass a null
        return new ScaleableBackground(stack, Optional.empty(), null);
    }

    /**
     * Constructor
     *
     * @param stack the stack (unscaled and unflattened)
     * @param scaleFactor an optional scale-factor to apply to the stack, and the bounding-box is
     *     extracted from this stack
     * @param interpolator interpolator to use for scaling stacks
     */
    private ScaleableBackground(
            Stack stack, Optional<ScaleFactor> scaleFactor, Interpolator interpolator) {
        try {
            this.stack = stack.mapChannel(Channel::projectMax);
        } catch (OperationFailedException e) {
            // Channels will always be the same size
            throw new AnchorImpossibleSituationException();
        }
        this.scaleFactor = scaleFactor;
        this.interpolator = interpolator;
    }

    /**
     * Creates a background from a stack with scaling
     *
     * @param stack the stack
     * @param scaleFactor what to scale the stack by
     * @param interpolator interpolator to use for scaling stacks
     * @return a newly created class
     */
    public static ScaleableBackground scaleBy(
            Stack stack, ScaleFactor scaleFactor, Interpolator interpolator) {
        // The interpolator is never used, so we can safely pass a null
        return new ScaleableBackground(stack, Optional.of(scaleFactor), interpolator);
    }

    /***
     * Number of channels in the stack
     *
     * @return number of channels
     */
    public int getNumberChannels() {
        return stack.getNumberChannels();
    }

    /**
     * Extracts a portion of a stack (flattened and maybe scaled) corresponding to a bounding-box
     *
     * @param box bounding-box representing the region
     * @return a stack showing only the bounding-box region
     * @throws CreateException
     */
    public Stack extractRegionFromStack(BoundingBox box) throws CreateException {
        try {
            if (scaleFactor.isPresent()) {
                return extractStackScaled(box, scaleFactor.get());
            } else {
                return extractStackUnscaled(box);
            }
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    public Extent extentAfterAnyScaling() {
        Extent extent = stack.extent();
        if (scaleFactor.isPresent()) {
            return extent.scaleXYBy(scaleFactor.get());
        } else {
            return extent;
        }
    }

    private Stack extractStackUnscaled(BoundingBox box) throws OperationFailedException {
        return stack.mapChannel(channel -> extractChannelUnscaled(channel, box));
    }

    private Stack extractStackScaled(BoundingBox box, ScaleFactor scaleFactor)
            throws OperationFailedException {
        // What would the bounding box look like in the unscaled window?
        BoundingBox boxUnscaled = box.scaleClipTo(scaleFactor.invert(), stack.extent());

        return stack.mapChannel(channel -> extractChannelScaled(channel, boxUnscaled, box));
    }

    private Channel extractChannelUnscaled(Channel channel, BoundingBox box) {
        return channelFor(extractVoxels(channel, box));
    }

    private Channel extractChannelScaled(
            Channel channel, BoundingBox boxUnscaled, BoundingBox boxScaled) {

        // Extract this region from the channels
        Voxels<?> voxelsUnscaled = extractVoxels(channel, boxUnscaled);

        // Scale it up to to the extent we want
        Voxels<?> voxelsScaled =
                voxelsUnscaled
                        .extract()
                        .resizedXY(boxScaled.extent().x(), boxScaled.extent().y(), interpolator);

        return channelFor(voxelsScaled);
    }

    private static Voxels<?> extractVoxels(Channel channel, BoundingBox box) {
        return channel.extract().region(box, false);
    }

    private Channel channelFor(Voxels<?> voxels) {
        return ChannelFactory.instance().create(voxels, stack.resolution());
    }
}
