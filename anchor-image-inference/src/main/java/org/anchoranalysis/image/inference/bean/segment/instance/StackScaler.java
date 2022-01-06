/*-
 * #%L
 * anchor-image-inference
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.inference.bean.segment.instance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.bean.nonbean.segment.SegmentationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.segment.DualScale;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizerExecutionTime;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Checks that the input {@link Stack} used for segmentation corresponds to expectations.
 *
 * <p>Additionally gray-scale stacks are converted into RGB, as is expected by the model.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class StackScaler {

    /**
     * Scales each channel in {@link Stack} to match the size expected by the model.
     *
     * @param stack the stack to scale.
     * @param scaleFactor the factor to scale by. This typically involves downscaling, but it can
     *     also be upscaling.
     * @param resizer an interpolator for resizing voxels.
     * @param executionTimeRecorder records the execution time of operations.
     * @return the converted stack.
     * @throws SegmentationFailedException if the stack has neither 1 nor 3 channels.
     */
    public static DualScale<Stack> scaleToModelSize(
            Stack stack,
            ScaleFactor scaleFactor,
            VoxelsResizer resizer,
            ExecutionTimeRecorder executionTimeRecorder)
            throws SegmentationFailedException {
        checkInput(stack);

        VoxelsResizer resizerRecording =
                new VoxelsResizerExecutionTime(
                        resizer, executionTimeRecorder, "As model input");

        if (stack.getNumberChannels() == 1) {
            Channel channelScaled =
                    scaleChannel(stack.getChannel(0), scaleFactor, resizerRecording);
            return new DualScale<>(stack, grayscaleToRGB(channelScaled));
        } else {
            try {
                Stack stackScaled =
                        stack.mapChannel(
                                channel ->
                                        scaleChannel(channel, scaleFactor, resizerRecording));
                return new DualScale<>(stack, stackScaled);
            } catch (OperationFailedException e) {
                throw new SegmentationFailedException(e);
            }
        }
    }

    /** Scales a single {@link Channel}. */
    private static Channel scaleChannel(
            Channel channel, ScaleFactor scaleFactor, VoxelsResizer resizer) {
        return channel.scaleXY(scaleFactor, resizer);
    }

    /** Checks that the {@link Stack} has the expected number of channels and z-slices. */
    private static Stack checkInput(Stack stack) throws SegmentationFailedException {
        if (stack.getNumberChannels() != 3 && stack.getNumberChannels() != 1) {
            throw new SegmentationFailedException(
                    String.format(
                            "Only RGB (3 channels) and grayscale (1 channel) stacks are supported by this algorithm. This stack has %d channels.",
                            stack.getNumberChannels()));
        }

        if (stack.dimensions().z() > 1) {
            throw new SegmentationFailedException("z-stacks are not supported by this algorithm");
        }

        return stack;
    }

    /** Converts a single-channel into an RGB image. */
    private static Stack grayscaleToRGB(Channel channel) {
        try {
            return new Stack(true, channel, channel.duplicate(), channel.duplicate());
        } catch (IncorrectImageSizeException | CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
