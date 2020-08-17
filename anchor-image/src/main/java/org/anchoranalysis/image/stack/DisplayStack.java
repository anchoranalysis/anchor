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

package org.anchoranalysis.image.stack;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.bufferedimage.BufferedImageFactory;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.stack.region.RegionExtracterFromDisplayStack;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.chnl.ChnlConverterChnlUpperLowerQuantileIntensity;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

/**
 * Byte Stack that contains 1 or 3 channels so that we cand display it as either or RGB 8-bit image
 *
 * <p>A voxelsConverter is optionally associated with each channel, used to convert the source
 * images into 8-bit.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayStack {

    private Stack delegate;
    private List<ChnlConverterAttached<Channel, ByteBuffer>> listConverters = new ArrayList<>();

    // START: factory methods

    // We don't allow

    public static DisplayStack create(Channel chnl) throws CreateException {
        DisplayStack ds = new DisplayStack();
        ds.delegate = new Stack(chnl);
        try {
            ds.addConvertersAsNeeded();
        } catch (SetOperationFailedException e) {
            throw new CreateException(e);
        }
        return ds;
    }

    public static DisplayStack create(Stack stack) throws CreateException {
        DisplayStack ds = new DisplayStack();
        ds.delegate = stack;
        ds.checkChnlNum(stack.getNumberChannels());
        try {
            ds.addConvertersAsNeeded();
        } catch (SetOperationFailedException e) {
            throw new CreateException(e);
        }
        return ds;
    }

    public static DisplayStack create(RGBStack rgbStack) throws CreateException {
        DisplayStack ds = new DisplayStack();
        ds.delegate = rgbStack.asStack();
        try {
            ds.addConvertersAsNeeded();
        } catch (SetOperationFailedException e) {
            throw new CreateException(e);
        }
        return ds;
    }

    // END: factory methods

    private void addConvertersAsNeeded() throws SetOperationFailedException {
        addEmptyConverters(getNumberChannels());
        for (int c = 0; c < getNumberChannels(); c++) {
            if (!delegate.getChannel(c)
                    .getVoxelDataType()
                    .equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                setConverterFor(
                        c, new ChnlConverterChnlUpperLowerQuantileIntensity(0.0001, 0.9999));
            }
        }
    }

    public int numNonNullConverters() {
        int a = 0;
        for (ChnlConverterAttached<Channel, ByteBuffer> c : listConverters) {
            if (c != null) {
                a++;
            }
        }
        return a;
    }

    // We don't need to worry about channel numbers
    // TODO let's switch this back to private
    private DisplayStack(
            Stack stack, List<ChnlConverterAttached<Channel, ByteBuffer>> listConverters)
            throws CreateException {
        delegate = stack;
        this.listConverters = listConverters;

        for (int c = 0; c < stack.getNumberChannels(); c++) {
            ChnlConverterAttached<Channel, ByteBuffer> cca = listConverters.get(c);
            if (cca != null) {
                try {
                    cca.attachObject(stack.getChannel(c));
                } catch (OperationFailedException e) {
                    throw new CreateException("Cannot attach the chnl to the converter");
                }
            }
        }
    }

    private void checkChnlNum(int numChnl) throws CreateException {

        if (numChnl > 3) {
            throw new CreateException(
                    String.format(
                            "Cannot convert to DisplayStack as there are %d channels. There must be 3 or less.",
                            numChnl));
        }

        if (numChnl == 2) {
            try {
                delegate.addBlankChannel();
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }
    }

    private void addEmptyConverters(int num) {
        for (int c = 0; c < num; c++) {
            listConverters.add(null);
        }
    }

    private void setConverterFor(int chnlNum, ChnlConverterAttached<Channel, ByteBuffer> converter)
            throws SetOperationFailedException {
        try {
            converter.attachObject(delegate.getChannel(chnlNum));
        } catch (OperationFailedException e) {
            throw new SetOperationFailedException(e);
        }
        listConverters.set(chnlNum, converter);
    }

    public ImageDimensions dimensions() {
        return delegate.dimensions();
    }

    public final int getNumberChannels() {
        return delegate.getNumberChannels();
    }

    // Only creates a new channel if needs be, otherwise reuses existing channel
    public Channel createChannel(int index, boolean alwaysNew) {

        Channel channel = delegate.getChannel(index);

        ChnlConverterAttached<Channel, ByteBuffer> converter = listConverters.get(index);

        if (converter != null) {
            ConversionPolicy policy =
                    alwaysNew
                            ? ConversionPolicy.ALWAYS_NEW
                            : ConversionPolicy.DO_NOT_CHANGE_EXISTING;
            return converter.convert(channel, policy);
        } else {
            if (!channel.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                // Datatype is not supported
                assert false;
            }

            return delegate.getChannel(index);
        }
    }

    // Always creates a new channel
    public Channel createChnlDuplicate(int index) {

        Channel chnl = delegate.getChannel(index);

        ChnlConverterAttached<Channel, ByteBuffer> converter = listConverters.get(index);

        if (converter != null) {
            return converter.convert(chnl, ConversionPolicy.ALWAYS_NEW);
        } else {
            if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                // Datatype is not supported
                assert false;
            }

            return delegate.getChannel(index).duplicate();
        }
    }

    // Always creates a new channel, but just capturing a portion of the channel
    public Channel createChannelDuplicateForBoundingBox(int index, BoundingBox box) {

        Channel chnl = delegate.getChannel(index);

        ChnlConverterAttached<Channel, ByteBuffer> converter = listConverters.get(index);

        Channel out =
                ChannelFactory.instance()
                        .createEmptyInitialised(
                                new ImageDimensions(box.extent(), chnl.dimensions().resolution()),
                                VoxelDataTypeUnsignedByte.INSTANCE);

        if (converter != null) {
            copyPixelsTo(index, box, out.voxels().asByte(), box.shiftToOrigin());
        } else {
            if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                // Datatype is not supported
                assert false;
            }

            delegate.getChannel(index)
                    .voxels()
                    .copyVoxelsTo(box, out.voxels(), box.shiftToOrigin());
        }
        return out;
    }

    /**
     * Derives a {@link Stack} from the display-stack that will be converted to 8-bit if necessary.
     *
     * @param alwaysNew iff TRUE channels are always created new during conversion, otherwise only
     *     if needed
     * @return a stack with either 1 or 3 channels (depending on what's passed into display-stack),
     *     all of which are unsigned 8-bit.
     */
    public Stack deriveStack(boolean alwaysNew) {
        Stack stackOut = new Stack();
        for (int c = 0; c < delegate.getNumberChannels(); c++) {
            try {
                stackOut.addChannel(createChannel(c, alwaysNew));
            } catch (IncorrectImageSizeException e) {
                throw new AnchorImpossibleSituationException();
            }
        }
        return stackOut;
    }

    public Stack createStackDuplicate() {
        Stack stackOut = new Stack();
        for (int c = 0; c < delegate.getNumberChannels(); c++) {
            try {
                stackOut.addChannel(createChnlDuplicate(c));
            } catch (IncorrectImageSizeException e) {
                assert false;
            }
        }
        return stackOut;
    }

    /** Copies pixels on a particular channel to an output buffer */
    public void copyPixelsTo(
            int chnlIndex,
            BoundingBox sourceBox,
            Voxels<ByteBuffer> voxelsDestination,
            BoundingBox destinationBox) {

        Channel channel = delegate.getChannel(chnlIndex);

        ChnlConverterAttached<Channel, ByteBuffer> converter = listConverters.get(chnlIndex);

        if (converter != null) {
            BoundingBox allLocalBox = destinationBox.shiftToOrigin();

            VoxelsWrapper destBoxNonByte =
                    VoxelsFactory.instance().create(destinationBox.extent(), channel.getVoxelDataType());
            channel.voxels().copyVoxelsTo(sourceBox, destBoxNonByte, allLocalBox);

            Voxels<ByteBuffer> destBoxByte = VoxelsFactory.getByte().createInitialized(destinationBox.extent());
            converter.getVoxelsConverter().convertFrom(destBoxNonByte, destBoxByte);

            destBoxByte.extracter().boxCopyTo(allLocalBox, voxelsDestination, destinationBox);

        } else {
            if (!channel.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                // Datatype is not supported
                assert false;
            }

            delegate.getChannel(chnlIndex)
                    .voxels()
                    .asByte()
                    .extracter()
                    .boxCopyTo(sourceBox, voxelsDestination, destinationBox);
        }
    }

    public Optional<VoxelDataType> unconvertedDataType() {
        VoxelDataType dataType = delegate.getChannel(0).getVoxelDataType();
        // If they don't all have the same dataType we return null
        if (!delegate.allChannelsHaveType(dataType)) {
            return Optional.empty();
        }
        return Optional.of(dataType);
    }

    public int getUnconvertedVoxelAt(int channelIndex, Point3i point) {
        return delegate.getChannel(channelIndex).extracter().voxel(point);
    }

    public RegionExtracter createRegionExtracter() {
        return new RegionExtracterFromDisplayStack(delegate, listConverters);
    }

    public DisplayStack maxIntensityProj() throws OperationFailedException {
        try {
            return new DisplayStack(delegate.maximumIntensityProjection(), listConverters);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public DisplayStack extractSlice(int z) throws CreateException {
        return new DisplayStack(delegate.extractSlice(z), listConverters);
    }

    public BufferedImage createBufferedImage() throws CreateException {
        if (delegate.getNumberChannels() == 3) {
            return BufferedImageFactory.createRGB(
                    voxelsForChannel(0),
                    voxelsForChannel(1),
                    voxelsForChannel(2),
                    delegate.dimensions().extent());
        }
        return BufferedImageFactory.createGrayscale(voxelsForChannel(0));
    }

    public BufferedImage createBufferedImageBBox(BoundingBox box) throws CreateException {

        if (box.extent().z() != 1) {
            throw new CreateException("BBox must have a single pixel z-height");
        }

        if (delegate.getNumberChannels() == 3) {
            return BufferedImageFactory.createRGB(
                    voxelsForChannelBoundingBox(0, box),
                    voxelsForChannelBoundingBox(1, box),
                    voxelsForChannelBoundingBox(2, box),
                    box.extent());
        }
        return BufferedImageFactory.createGrayscale(voxelsForChannelBoundingBox(0, box));
    }

    private Voxels<ByteBuffer> voxelsForChannel(int chnlNum) {

        Channel chnl = delegate.getChannel(chnlNum);

        ChnlConverterAttached<Channel, ByteBuffer> converter = listConverters.get(chnlNum);

        if (converter != null) {
            return converter
                    .getVoxelsConverter()
                    .convertFrom(chnl.voxels(), VoxelsFactory.getByte());
        } else {
            if (!chnl.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                // Datatype is not supported
                assert false;
            }
            return chnl.voxels().asByte();
        }
    }

    @SuppressWarnings("unchecked")
    private Voxels<ByteBuffer> voxelsForChannelBoundingBox(int chnlNum, BoundingBox box) {

        Channel channel = delegate.getChannel(chnlNum);

        ChnlConverterAttached<Channel, ByteBuffer> converter = listConverters.get(chnlNum);

        Voxels<?> voxelsUnconverted = channel.extracter().region(box, true);

        if (converter != null) {
            return converter
                    .getVoxelsConverter()
                    .convertFrom(new VoxelsWrapper(voxelsUnconverted), VoxelsFactory.getByte());
        } else {
            if (!channel.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                // Datatype is not supported
                assert false;
            }
            return (Voxels<ByteBuffer>) voxelsUnconverted;
        }
    }
}
