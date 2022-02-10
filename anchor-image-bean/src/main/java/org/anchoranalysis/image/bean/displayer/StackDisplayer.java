package org.anchoranalysis.image.bean.displayer;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Converts from a {@link Channel} or {@link Stack} to a {@link DisplayStack} so that it can be
 * displayed.
 *
 * <p>Only images in two forms may be displayed:
 *
 * <ul>
 *   <li>a single-channeled image (grayscale) with unsigned 8-bit voxel data type
 *   <li>a RGB image with three channels, each with unsigned 8-bit voxel data type.
 * </ul>
 *
 * <p>Any other form of image, will be converted into one of the above (the former only if there is
 * a single-channel).
 *
 * <p>If there are more than three channels, the first three channels are taken.
 *
 * <p>If there are only two channels, they occupy respectively the red and blue channels (green is
 * left blank).
 *
 * @author Owen Feehan
 */
public abstract class StackDisplayer extends AnchorBean<StackDisplayer> {

    /**
     * Creates from a {@link Channel}.
     *
     * @param channel the stack to create from.
     * @return a newly created {@link DisplayStack}, after applying any applicable conversion.
     * @throws CreateException if a converter cannot be associated with a particular channel.
     */
    public DisplayStack deriveFrom(Channel channel) throws CreateException {
        return new DisplayStack(new Stack(channel), false, this::createConverterFor);
    }

    /**
     * Derives a {@link DisplayStack} from a {@link RGBStack}.
     *
     * @param stack the stack to create from, which should have either 1 or 3 channels
     *     (corresponding to RGB).
     * @return a newly created {@link DisplayStack}, after applying any applicable conversion.
     * @throws CreateException with an incorrect number of channels, or if a converter cannot be
     *     associated with a particular channel.
     */
    public DisplayStack deriveFrom(Stack stack) throws CreateException {
        return new DisplayStack(stack, stack.getNumberChannels() > 1, this::createConverterFor);
    }

    /**
     * Derives a {@link DisplayStack} from a {@link RGBStack}.
     *
     * @param rgbStack the stack to create from.
     * @return a newly created {@link DisplayStack}, after applying any applicable conversion.
     * @throws CreateException if a converter cannot be associated with a particular channel.
     */
    public DisplayStack deriveFrom(RGBStack rgbStack) throws CreateException {
        return new DisplayStack(rgbStack.asStack(), true, this::createConverterFor);
    }

    /**
     * Determines what kind of converter to use for a particular channel, to map it to an unsigned
     * 8-bit channel.
     *
     * @param dataType the voxel-data type that must be converted to unsigned 8-bit.
     * @return a newly created {@link ChannelConverterAttached} that can convert from channels with
     *     voxel-type {@code dataType} to unsigned 8-bit.
     */
    protected abstract ChannelConverterAttached<Channel, UnsignedByteBuffer> createConverterFor(
            VoxelDataType dataType);
}
