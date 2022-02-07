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
package org.anchoranalysis.image.io.stack.output.box;

import java.util.Optional;
import javax.annotation.Nullable;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A stack that can be used as a background (and maybe scaled).
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
    /** Image to extract a bounding-box region from, to form the background. */
    private final DisplayStack stack;

    /**
     * If defined, the stack is scaled (and interpolated) by this factor before a bounding box is
     * extracted.
     */
    private final Optional<ScaleFactor> scaleFactor;

    /** Interpolator to use for scaling stacks, if necessary */
    @Nullable private final VoxelsResizer resizer;
    // END REQUIRED ARGUMENTS

    /**
     * Creates a background from a stack without any scaling.
     *
     * @param stack the stack
     * @return a newly created class
     */
    public static ScaleableBackground noScaling(DisplayStack stack) {
        // The interpolator is never used, so we can safely pass a null
        return new ScaleableBackground(stack, Optional.empty(), null);
    }

    /**
     * Creates from a {@link DisplayStack}.
     *
     * @param stack the stack (unscaled and unflattened).
     * @param scaleFactor an optional scale-factor to apply to the stack, and the bounding-box is
     *     extracted from this stack.
     * @param resizer interpolator to use for scaling stacks.
     */
    private ScaleableBackground(
            DisplayStack stack, Optional<ScaleFactor> scaleFactor, VoxelsResizer resizer) {
        this.stack = stack.projectMax();
        this.scaleFactor = scaleFactor;
        this.resizer = resizer;
    }

    /**
     * Creates a background from a stack with scaling.
     *
     * @param stack the stack.
     * @param scaleFactor what to scale the stack by.
     * @param resizer an interpolator for resizing voxels.
     * @return a newly created {@link ScaleableBackground}.
     */
    public static ScaleableBackground scaleBy(
            DisplayStack stack, ScaleFactor scaleFactor, VoxelsResizer resizer) {
        // The interpolator is never used, so we can safely pass a null
        return new ScaleableBackground(stack, Optional.of(scaleFactor), resizer);
    }

    /***
     * Number of channels in the background.
     *
     * @return number of channels.
     */
    public int getNumberChannels() {
        return stack.getNumberChannels();
    }

    /**
     * Extracts a portion of a stack (flattened and maybe scaled) corresponding to a bounding-box
     *
     * @param box bounding-box representing the region.
     * @return a stack showing only the bounding-box region.
     * @throws OperationFailedException if the operation cannot complete successfully.
     */
    public Stack extractRegionFromStack(BoundingBox box) throws OperationFailedException {
        if (scaleFactor.isPresent()) {
            return extractStackScaled(box, scaleFactor.get());
        } else {
            return extractStackUnscaled(box);
        }
    }

    /**
     * The size of the background after any scaling has occurred.
     *
     * @return the size.
     */
    public Extent sizeAfterAnyScaling() {
        Extent extent = stack.extent();
        if (scaleFactor.isPresent()) {
            return extent.scaleXYBy(scaleFactor.get(), true);
        } else {
            return extent;
        }
    }

    /**
     * Does the display-stack contain an RGB image?
     *
     * @return true if the contained image is RGB, false if it is grayscale.
     */
    public boolean isRGB() {
        return stack.isRGB();
    }

    /**
     * Extract a {@link Stack} representing the bounding-box region, when no scaling-factor is
     * applied.
     */
    private Stack extractStackUnscaled(BoundingBox box) throws OperationFailedException {
        return stack.getStack().mapChannel(channel -> extractChannelUnscaled(channel, box));
    }

    /**
     * Extract a {@link Stack} representing the bounding-box region, when a scaling-factor is
     * applied.
     */
    private Stack extractStackScaled(BoundingBox box, ScaleFactor scaleFactor)
            throws OperationFailedException {
        // What would the bounding box look like in the unscaled window?
        BoundingBox boxUnscaled = box.scaleClampTo(scaleFactor.invert(), stack.extent());

        return stack.getStack()
                .mapChannel(channel -> extractChannelScaled(channel, boxUnscaled, box));
    }

    /**
     * Extract a {@link Channel} representing the bounding-box region, when no scaling-factor is
     * applied.
     */
    private Channel extractChannelUnscaled(Channel channel, BoundingBox box) {
        return channelFor(extractBoundingBox(channel, box));
    }

    /**
     * Extract a {@link Channel} representing the bounding-box region, when a scaling-factor is
     * applied.
     */
    private Channel extractChannelScaled(
            Channel channel, BoundingBox boxUnscaled, BoundingBox boxScaled) {

        // Extract this region from the channels
        Voxels<?> voxelsUnscaled = extractBoundingBox(channel, boxUnscaled);

        // Scale it up to to the extent we want
        Voxels<?> voxelsScaled =
                voxelsUnscaled
                        .extract()
                        .resizedXY(boxScaled.extent().x(), boxScaled.extent().y(), resizer);

        return channelFor(voxelsScaled);
    }

    /** Create a {@link Channel} for particular {@link Voxels}. */
    private Channel channelFor(Voxels<?> voxels) {
        return ChannelFactory.instance().create(voxels, stack.resolution());
    }

    /** Extracts the voxels from {@code channel} that lie inside {@code box}. */
    private static Voxels<?> extractBoundingBox(Channel channel, BoundingBox box) {
        return channel.extract().region(box, false);
    }
}
