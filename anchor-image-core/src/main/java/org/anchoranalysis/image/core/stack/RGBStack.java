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
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * A stack with exactly three channels, respectively for <i>red</i>, <i>green</i> and <i>blue</i>
 * colors.
 *
 * @author Owen Feehan
 */
public class RGBStack {

    private final Stack stack;

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
        if (numberChannels == 3) {
            this.stack = stack;
        } else if (numberChannels == 1) {
            this.stack = convertGrayscaleIntoColor(stack);
        } else {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "Cannot create a RGB-stack from this stack, as it has %d number of channels. Only a single-channel or three channels (representing red, green, blue) are supported.",
                            stack.getNumberChannels()));
        }
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
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index
     *     >= size()})
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
     * Assigns a {@link RGBColor} to the respective channels for a single voxel.
     *
     * @param point identifies which voxel to assign the color value to.
     * @param color the color.
     * @throws IllegalArgumentException if the stack has a channel that is not {@link
     *     UnsignedByteVoxelType}.
     */
    public void assignColor(Point3i point, RGBColor color) {
        Preconditions.checkArgument(stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE));
        writePoint(point, stack.getChannel(0), color.getRed());
        writePoint(point, stack.getChannel(1), color.getGreen());
        writePoint(point, stack.getChannel(2), color.getBlue());
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

    private static Stack convertGrayscaleIntoColor(Stack stack) {
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

    private static void writePoint(Point3i point, Channel channel, int toWrite) {
        channel.assignValue(toWrite).toVoxel(point);
    }
}
