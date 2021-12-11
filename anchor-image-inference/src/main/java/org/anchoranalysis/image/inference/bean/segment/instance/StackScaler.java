package org.anchoranalysis.image.inference.bean.segment.instance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.segment.DualScale;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.io.imagej.interpolator.InterpolatorImageJ;
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

    // This is used for downscaling as it's fast.
    private static final Interpolator INTERPOLATOR = new InterpolatorImageJ();

    /**
     * Scales each channel in {@link Stack} to match the size expected by the model.
     *
     * @param stack the stack to scale.
     * @param scaleFactor the factor to scale by. This typically involves downscaling, but it can
     *     also be upscaling.
     * @return the converted stack.
     * @throws SegmentationFailedException if the stack has neither 1 nor 3 channels.
     */
    public static DualScale<Stack> scaleToModelSize(Stack stack, ScaleFactor scaleFactor)
            throws SegmentationFailedException {
        checkInput(stack);
        if (stack.getNumberChannels() == 1) {
            Channel channelScaled = scaleChannel(stack.getChannel(0), scaleFactor);
            return new DualScale<>(stack, grayscaleToRGB(channelScaled));
        } else {
            try {
                Stack stackScaled = stack.mapChannel(channel -> scaleChannel(channel, scaleFactor));
                return new DualScale<>(stack, stackScaled);
            } catch (OperationFailedException e) {
                throw new SegmentationFailedException(e);
            }
        }
    }

    /** Scales a single {@link Channel}. */
    private static Channel scaleChannel(Channel channel, ScaleFactor scaleFactor) {
        return channel.scaleXY(scaleFactor, INTERPOLATOR);
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
            // We deliberately avoid duplicating the channels, as we know they will never be written
            // to.
            return new Stack(true, channel, channel, channel);
        } catch (IncorrectImageSizeException | CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
