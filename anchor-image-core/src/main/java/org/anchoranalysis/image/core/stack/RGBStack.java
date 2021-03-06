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
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * A stack with exactly three channels, respectively for Red, Green and Blue colors.
 *
 * @author Owen Feehan
 */
public class RGBStack {

    private final Stack stack;

    /**
     * Creates a particularly-sized stack with all channels initialized to 0.
     *
     * @param dimensions dimensions of each channel
     * @param factory factory to create the channel
     */
    public RGBStack(Dimensions dimensions, ChannelFactorySingleType factory) {
        stack = new Stack(dimensions, factory, 3, true);
    }

    /**
     * Creates from an existing stack (which must have 1 or 3 channels).
     *
     * <p>The channel order is: red, green, blue.
     *
     * <p>A single channel is treated as grayscale, and duplicated to form red, green, blue
     * channels.
     *
     * @param stack
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
     * @param source where to copy from
     */
    private RGBStack(RGBStack source) {
        stack = source.stack.duplicateDeep();
    }

    public RGBStack(Channel red, Channel green, Channel blue) throws IncorrectImageSizeException {
        stack = new Stack(true, red, green, blue);
    }

    public Channel red() {
        return stack.getChannel(0);
    }

    public Channel green() {
        return stack.getChannel(1);
    }

    public Channel blue() {
        return stack.getChannel(2);
    }

    public Channel channelAt(int index) {
        return stack.getChannel(index);
    }

    public Dimensions dimensions() {
        return stack.dimensions();
    }

    public RGBStack extractSlice(int z) {
        return new RGBStack(stack.extractSlice(z));
    }

    public Stack asStack() {
        return stack;
    }

    public DisplayStack backgroundStack() throws CreateException {
        return DisplayStack.create(this);
    }

    public RGBStack duplicate() {
        return new RGBStack(this);
    }

    public boolean allChannelsHaveType(VoxelDataType channelDataType) {
        return stack.allChannelsHaveType(channelDataType);
    }

    private static void writePoint(Point3i point, Channel channel, int toWrite) {
        channel.assignValue(toWrite).toVoxel(point);
    }

    // Only supports 8-bit
    public void writeRGBPoint(Point3i point, RGBColor color) {
        assert (stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE));
        writePoint(point, stack.getChannel(0), color.getRed());
        writePoint(point, stack.getChannel(1), color.getGreen());
        writePoint(point, stack.getChannel(2), color.getBlue());
    }

    // Only supports 8-bit
    public void writeRGBMaskToSlice(
            ObjectMask object,
            BoundingBox box,
            RGBColor color,
            Point3i pointGlobal,
            int zLocal,
            ReadableTuple3i maxGlobal) {
        Preconditions.checkArgument(pointGlobal.z() >= 0);
        Preconditions.checkArgument(stack.getNumberChannels() == 3);
        Preconditions.checkArgument(stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE));

        byte objectMaskOn = object.binaryValuesByte().getOnByte();

        UnsignedByteBuffer inArr = object.sliceBufferLocal(zLocal);

        UnsignedByteBuffer red = extractBuffer(0, pointGlobal.z());
        UnsignedByteBuffer green = extractBuffer(1, pointGlobal.z());
        UnsignedByteBuffer blue = extractBuffer(2, pointGlobal.z());

        Extent eMask = object.boundingBox().extent();

        for (pointGlobal.setY(box.cornerMin().y());
                pointGlobal.y() <= maxGlobal.y();
                pointGlobal.incrementY()) {

            for (pointGlobal.setX(box.cornerMin().x());
                    pointGlobal.x() <= maxGlobal.x();
                    pointGlobal.incrementX()) {

                int objectMaskOffset =
                        eMask.offset(
                                pointGlobal.x() - object.boundingBox().cornerMin().x(),
                                pointGlobal.y() - object.boundingBox().cornerMin().y());

                if (inArr.getRaw(objectMaskOffset) == objectMaskOn) {
                    writeRGBColorToByteArray(
                            color, pointGlobal, stack.getChannel(0).dimensions(), red, blue, green);
                }
            }
        }
    }

    private UnsignedByteBuffer extractBuffer(int channelIndex, int zIndex) {
        return stack.getChannel(channelIndex).voxels().asByte().slice(zIndex).buffer();
    }

    private static Stack convertGrayscaleIntoColor(Stack stack) {
        Channel orig = stack.getChannel(0);
        Stack out = new Stack(orig);
        try {
            out.addChannel(orig.duplicate());
            out.addChannel(orig.duplicate());
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
        return out;
    }

    public Extent extent() {
        return stack.extent();
    }

    private static void writeRGBColorToByteArray(
            RGBColor color,
            Point3i point,
            Dimensions dimensions,
            UnsignedByteBuffer red,
            UnsignedByteBuffer blue,
            UnsignedByteBuffer green) {
        int index = dimensions.offsetSlice(point);
        red.putUnsigned(index, color.getRed());
        green.putUnsigned(index, color.getGreen());
        blue.putUnsigned(index, color.getBlue());
    }
}
