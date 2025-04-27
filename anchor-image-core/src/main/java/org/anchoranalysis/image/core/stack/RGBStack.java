/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.stack;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.checked.CheckedUnaryOperator;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.image.voxel.object.DeriveObjectFromPoints;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * A stack with exactly three channels, respectively for <i>red</i>, <i>green</i> and <i>blue</i>
 * colors.
 *
 * @author Owen Feehan
 */
public class RGBStack implements Iterable<Channel> {

    private final Stack stack;

    /**
     * Creates a particularly-sized stack with <i>unsigned byte</i> voxel data type, with all voxels
     * initialized to 0.
     *
     * @param extent size of each channel.
     */
    public RGBStack(Extent extent) {
        this(new Dimensions(extent), ChannelFactory.instance().get(UnsignedByteVoxelType.INSTANCE));
    }

    /**
     * Creates a particularly-sized stack with <i>unsigned byte</i> voxel data type, with all voxels
     * initialized to a specific {@link Color}.
     *
     * @param extent size of each channel.
     * @param color the color to assign to all voxels.
     */
    public RGBStack(Extent extent, Color color) {
        this(extent, new RGBColor(color));
    }

    /**
     * Creates a particularly-sized stack with <i>unsigned byte</i> voxel data type, with all voxels
     * initialized to a specific {@link RGBColor}.
     *
     * @param extent size of each channel.
     * @param color the color to assign to all voxels.
     */
    public RGBStack(Extent extent, RGBColor color) {
        this(extent);
        assignAllVoxels(color);
    }

    /**
     * Creates a particularly-sized stack with all channels initialized to 0.
     *
     * @param extent size of each channel.
     * @param factory factory to create the channel.
     */
    public RGBStack(Extent extent, ChannelFactorySingleType factory) {
        this(new Dimensions(extent), factory);
    }

    /**
     * Creates a particularly-sized stack with all channels initialized to 0.
     *
     * @param dimensions dimensions of each channel.
     * @param factory factory to create the channel.
     */
    public RGBStack(Dimensions dimensions, ChannelFactorySingleType factory) {
        try {
            stack = new Stack(dimensions, factory, 3, true);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Creates from an existing stack (which must have 1 or 3 channels).
     *
     * <p>The channel order is: red, green, blue.
     *
     * <p>A single channel is treated as grayscale, and duplicated to form red, green, blue
     * channels.
     *
     * @param stack the stack.
     */
    public RGBStack(Stack stack) {
        int numberChannels = stack.getNumberChannels();
        this.stack =
                switch (numberChannels) {
                    case 3 -> stack;
                    case 1 -> duplicateToThreeChannels(stack);
                    default ->
                            throw new AnchorFriendlyRuntimeException(
                                    String.format(
                                            "Cannot create a RGB-stack from this stack, as it has %d number of channels. Only a single-channel or three channels (representing red, green, blue) are supported.",
                                            numberChannels));
                };
    }

    /**
     * Copy constructor, deep copies channels.
     *
     * @param source where to copy from.
     */
    private RGBStack(RGBStack source) {
        stack = source.stack.duplicateDeep();
    }

    /**
     * Create with {@link Channel}s for each color.
     *
     * @param red the <b>red</b> channel.
     * @param green the <b>green</b> channel.
     * @param blue the <b>blue</b> channel.
     * @throws IncorrectImageSizeException if the channels are not uniformly sized.
     */
    public RGBStack(Channel red, Channel green, Channel blue) throws IncorrectImageSizeException {
        try {
            stack = new Stack(true, red, green, blue);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Produces a new stack with a particular mapping applied to each channel.
     *
     * <p>The function applied to the channel should ensure it produces uniform sizes.
     *
     * @param mapping performs an operation on a channel and produces a modified channel (or a
     *     different one entirely).
     * @return a new stack (after any modification by {@code mapping}) preserving the channel order.
     * @throws OperationFailedException if the channels produced have non-uniform sizes.
     */
    public RGBStack mapChannel(CheckedUnaryOperator<Channel, OperationFailedException> mapping)
            throws OperationFailedException {
        return new RGBStack(stack.mapChannel(mapping));
    }

    /**
     * The <i>red</i> channel.
     *
     * @return the red channel.
     */
    public Channel red() {
        return stack.getChannel(0);
    }

    /**
     * The <i>green</i> channel.
     *
     * @return the green channel.
     */
    public Channel green() {
        return stack.getChannel(1);
    }

    /**
     * The <i>blue</i> channel.
     *
     * @return the blue channel.
     */
    public Channel blue() {
        return stack.getChannel(2);
    }

    /**
     * Returns the channel at a particular position in the stack.
     *
     * @param index the index (zero-indexed).
     * @return the respective channel.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >=
     *     size()})
     */
    public Channel getChannel(int index) {
        return stack.getChannel(index);
    }

    /**
     * The dimensions of all channels in the stack.
     *
     * @return the dimensions.
     */
    public Dimensions dimensions() {
        return stack.dimensions();
    }

    /**
     * Extract a particular z-slice from the {@link Stack} as a new stack.
     *
     * @param z the index in the Z-dimension of the slice to extract.
     * @return the extracted slice, as a new {@link Stack} but reusing the existing voxels.
     */
    public RGBStack extractSlice(int z) {
        return new RGBStack(stack.extractSlice(z));
    }

    /**
     * Exposes the underlying stack, storing the three RGB channels in respective order.
     *
     * @return the stack.
     */
    public Stack asStack() {
        return stack;
    }

    /**
     * A deep copy.
     *
     * @return a deep copy of the current instance.
     */
    public RGBStack duplicate() {
        return new RGBStack(this);
    }

    /**
     * Determines if all channels have a specific data-type.
     *
     * @param channelDataType the specific data-type.
     * @return true iff all channels have {@code channelDataType} as their voxel data-type.
     */
    public boolean allChannelsHaveType(VoxelDataType channelDataType) {
        return stack.allChannelsHaveType(channelDataType);
    }

    /**
     * Assigns a {@link RGBColor} to a single voxel in the respective channels.
     *
     * @param point identifies which voxel to assign the color value to.
     * @param color the color.
     * @throws IllegalArgumentException if the stack has a channel that is not {@link
     *     UnsignedByteVoxelType}.
     */
    public void assignVoxel(Point3i point, RGBColor color) {
        Preconditions.checkArgument(stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE));
        assignToVoxel(point, stack.getChannel(0), color.getRed());
        assignToVoxel(point, stack.getChannel(1), color.getGreen());
        assignToVoxel(point, stack.getChannel(2), color.getBlue());
    }

    /**
     * Assigns a {@link RGBColor} to all voxels in the respective channels.
     *
     * @param color the color.
     * @throws IllegalArgumentException if the stack has a channel that is not {@link
     *     UnsignedByteVoxelType}.
     */
    public void assignAllVoxels(RGBColor color) {
        Preconditions.checkArgument(stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE));
        assignToAllVoxels(stack.getChannel(0), color.getRed());
        assignToAllVoxels(stack.getChannel(1), color.getGreen());
        assignToAllVoxels(stack.getChannel(2), color.getBlue());
    }

    /**
     * Gets the color at a particular voxel.
     *
     * <p>Note that it is inefficient to call this method on voxels <i>repeatedly</i>, as it is
     * heavy on memory allocation on the heap, and makes inefficient usage of buffer iteration. A
     * new {@link RGBColor} is created with each call. Prefer iterating the voxels via {@link
     * #getChannel(int)}.
     *
     * @param point locates the voxel (zero-indexed) in the stack.
     * @return a newly created {@link RGBColor}, indicating the color at a particular voxel.
     */
    public RGBColor colorAtVoxel(ReadableTuple3i point) {
        int red = getVoxel(point, stack.getChannel(0));
        int green = getVoxel(point, stack.getChannel(1));
        int blue = getVoxel(point, stack.getChannel(2));
        return new RGBColor(red, green, blue);
    }

    /**
     * Extracts an {@link ObjectMask} from a {@link Channel} of all voxels that have a particular
     * color.
     *
     * <p>This operation is only supported when all channels have type unsigned-byte.
     *
     * <p>The bounding-box of the created {@link ObjectMask} will fit the voxels as maximally
     * tightly as possible.
     *
     * @param color the color to search for.
     * @return an {@link ObjectMask} describing all voxels with this color, if any exist. If no
     *     exist, then {@link Optional#empty()}.
     * @throws OperationFailedException if any channel has a data-type other than unsigned-byte.
     */
    public Optional<ObjectMask> objectWithColor(RGBColor color) throws OperationFailedException {

        if (!allChannelsHaveType(UnsignedByteVoxelType.INSTANCE)) {
            throw new OperationFailedException(
                    "At least one channel has an unsupported data-type. This operation is only supported when all channels have type unsigned-byte.");
        }

        DeriveObjectFromPoints deriver = new DeriveObjectFromPoints();

        byte rawRed = (byte) color.getRed();
        byte rawGreen = (byte) color.getGreen();
        byte rawBlue = (byte) color.getBlue();

        IterateVoxelsAll.withThreeBuffers(
                voxelsAsByte(0),
                voxelsAsByte(1),
                voxelsAsByte(2),
                (Point3i point,
                        UnsignedByteBuffer buffer1,
                        UnsignedByteBuffer buffer2,
                        UnsignedByteBuffer buffer3,
                        int offset1,
                        int offset2,
                        int offset3) -> {
                    if (buffer1.getRaw(offset1) == rawRed
                            && buffer2.getRaw(offset2) == rawGreen
                            && buffer3.getRaw(offset3) == rawBlue) {
                        deriver.add(point);
                    }
                });

        return deriver.deriveObject();
    }

    /**
     * The width and height and depth of the image.
     *
     * <p>i.e. the size of each of the three possible dimensions.
     *
     * @return the extent.
     */
    public Extent extent() {
        return stack.extent();
    }

    /**
     * A buffer corresponding to a particular z-slice of a particular channel.
     *
     * <p>This buffer is either a NIO class or another class that wraps the underlying array storing
     * voxel intensities.
     *
     * @param channelIndex the index (beginning at 0) of the respective channel.
     * @param zIndex the index (beginning at 0) of the respective z-slice.
     * @return the corresponding buffer for {@code z}.
     */
    public UnsignedByteBuffer sliceBuffer(int channelIndex, int zIndex) {
        return stack.getChannel(channelIndex).voxels().asByte().slice(zIndex).buffer();
    }

    @Override
    public Iterator<Channel> iterator() {
        return stack.iterator();
    }

    /** The {@link Voxels} for a particular channel, as {@link UnsignedByteVoxelType}. */
    private Voxels<UnsignedByteBuffer> voxelsAsByte(int channelIndex) {
        return stack.getChannel(channelIndex).voxels().asByte();
    }

    /** Convert a single-channeled stack into three-channel stack, by duplicating. */
    private static Stack duplicateToThreeChannels(Stack stack) {
        Channel source = stack.getChannel(0);
        Stack out = new Stack(source);
        try {
            out.addChannel(source.duplicate());
            out.addChannel(source.duplicate());
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
        return out;
    }

    /** Assigns a value to a specific voxel a {@link Channel}. */
    private static void assignToVoxel(Point3i point, Channel channel, int toWrite) {
        channel.assignValue(toWrite).toVoxel(point);
    }

    /** Assigns a value to all voxels in a {@link Channel}. */
    private static void assignToAllVoxels(Channel channel, int toWrite) {
        channel.assignValue(toWrite).toAll();
    }

    /** Gets a value at a particular voxel in a {@link Channel}. */
    private static int getVoxel(ReadableTuple3i point, Channel channel) {
        return channel.extract().voxel(point);
    }
}
