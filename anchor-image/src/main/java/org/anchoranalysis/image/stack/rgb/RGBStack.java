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

package org.anchoranalysis.image.stack.rgb;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.nio.ByteBuffer;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

/**
 * A stack with exactly three channels, respectively for Red, Green and Blue colors.
 * 
 * TODO create three explicit channels for red, green, blue only if needed, otherwise keep as grayscale.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class RGBStack {

    private Stack channels;

    /**
     * Constructor - creates a particularly-sized stack with all channels initialized to 0.
     * 
     * @param dimensions dimensions of each channel
     * @param factory factory to create the channel
     */
    public RGBStack(ImageDimensions dimensions, ChannelFactorySingleType factory) {
        channels = new Stack(dimensions, factory, 3);
    }
    
    /**
     * Constructor - creates a RGB stack from an existing stack (which must have 1 or 3 channels)
     * <p>
     * The channel order is: red, green, blue.
     * <p>
     * A single channel is treated as grayscale, and duplicated to form red, green, blue channels.
     * 
     * @param stack
     */
    public RGBStack(Stack stack) {
        int numberChannels = stack.getNumberChannels();
        if (numberChannels==3) {
            channels = stack;
        } else if (numberChannels==1) {
            channels = convertGrayscaleIntoColor(stack);
        } else {
            throw new AnchorFriendlyRuntimeException(
              String.format(
                 "Cannot create a RGB-stack from this stack, as it has %d number of channels. Only a single-channel or three channels (representing red, green, blue) are supported.",
                 stack.getNumberChannels()
              )
            );
        }
    }

    /**
     * Copy constructor
     * 
     * @param source where to copy from
     */
    private RGBStack(RGBStack source) {
        channels = source.channels.duplicate();
    }

    public RGBStack(Channel red, Channel green, Channel blue) throws IncorrectImageSizeException {
        channels = new Stack();
        channels.addChannel(red);
        channels.addChannel(green);
        channels.addChannel(blue);
    }

    public Channel getRed() {
        return channels.getChannel(0);
    }

    public Channel getGreen() {
        return channels.getChannel(1);
    }

    public Channel getBlue() {
        return channels.getChannel(2);
    }

    public Channel getChnl(int index) {
        return channels.getChannel(index);
    }

    public ImageDimensions getDimensions() {
        return channels.getDimensions();
    }

    public RGBStack extractSlice(int z) {
        RGBStack out = new RGBStack();
        out.channels = channels.extractSlice(z);
        return out;
    }

    public Stack asStack() {
        return channels;
    }

    public DisplayStack backgroundStack() throws CreateException {
        return DisplayStack.create(this);
    }

    public RGBStack duplicate() {
        return new RGBStack(this);
    }

    public boolean allChnlsHaveType(VoxelDataType chnlDataType) {
        return channels.allChannelsHaveType(chnlDataType);
    }

    private static void writePoint(Point3i point, Channel chnl, byte toWrite) {
        int index = chnl.getDimensions().getExtent().offset(point.getX(), point.getY());
        chnl.getVoxelBox().asByte().getPixelsForPlane(point.getZ()).buffer().put(index, toWrite);
    }

    // Only supports 8-bit
    public void writeRGBPoint(Point3i point, RGBColor color) {
        assert (channels.allChannelsHaveType(VoxelDataTypeUnsignedByte.INSTANCE));
        writePoint(point, channels.getChannel(0), (byte) color.getRed());
        writePoint(point, channels.getChannel(1), (byte) color.getGreen());
        writePoint(point, channels.getChannel(2), (byte) color.getBlue());
    }

    // Only supports 8-bit
    public void writeRGBMaskToSlice(
            ObjectMask mask,
            BoundingBox bbox,
            RGBColor c,
            Point3i pointGlobal,
            int zLocal,
            ReadableTuple3i maxGlobal) {
        Preconditions.checkArgument(pointGlobal.getZ() >= 0);
        Preconditions.checkArgument(channels.getNumberChannels() == 3);
        Preconditions.checkArgument(channels.allChannelsHaveType(VoxelDataTypeUnsignedByte.INSTANCE));

        byte maskOn = mask.getBinaryValuesByte().getOnByte();

        ByteBuffer inArr = mask.getVoxelBox().getPixelsForPlane(zLocal).buffer();

        ByteBuffer red = extractBuffer(0, pointGlobal.getZ());
        ByteBuffer green = extractBuffer(1, pointGlobal.getZ());
        ByteBuffer blue = extractBuffer(2, pointGlobal.getZ());

        Extent eMask = mask.getBoundingBox().extent();

        for (pointGlobal.setY(bbox.cornerMin().getY());
                pointGlobal.getY() <= maxGlobal.getY();
                pointGlobal.incrementY()) {

            for (pointGlobal.setX(bbox.cornerMin().getX());
                    pointGlobal.getX() <= maxGlobal.getX();
                    pointGlobal.incrementX()) {

                int maskOffset =
                        eMask.offset(
                                pointGlobal.getX() - mask.getBoundingBox().cornerMin().getX(),
                                pointGlobal.getY() - mask.getBoundingBox().cornerMin().getY());

                if (inArr.get(maskOffset) != maskOn) {
                    continue;
                }

                RGBOutputUtils.writeRGBColorToByteArr(
                        c, pointGlobal, channels.getChannel(0).getDimensions(), red, blue, green);
            }
        }
    }

    private ByteBuffer extractBuffer(int chnlIndex, int zIndex) {
        return channels.getChannel(chnlIndex)
                .getVoxelBox()
                .asByte()
                .getPlaneAccess()
                .getPixelsForPlane(zIndex)
                .buffer();
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

}
