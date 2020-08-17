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

import com.google.common.base.Functions;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
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
 * Stack that contains 1 or 3 channels so that we and display it as either or RGB unsigned 8-bit
 * image
 *
 * <p>A converter is optionally associated with each channel, used to convert the source images into
 * unsigned 8-bit.
 *
 * @author Owen Feehan
 */
public class DisplayStack {

    private static final double QUANTILE_LOWER = 0.0001;
    private static final double QUANTILE_UPPER = 0.9999;

    private final Stack stack;
    private final List<Optional<ChnlConverterAttached<Channel, ByteBuffer>>> listConverters;
    private final ChannelMapper mapper;

    // START: constructors
    private DisplayStack(Stack stack) {
        this.stack = stack;
        this.listConverters = new ArrayList<>();
        this.mapper = createChannelMapper();
    }

    // We don't need to worry about channel numbers
    private DisplayStack(
            Stack stack, List<Optional<ChnlConverterAttached<Channel, ByteBuffer>>> listConverters)
            throws CreateException {
        this.stack = stack;
        this.listConverters = listConverters;
        this.mapper = createChannelMapper();

        for (int index = 0; index < stack.getNumberChannels(); index++) {
            Optional<ChnlConverterAttached<Channel, ByteBuffer>> converter =
                    listConverters.get(index);
            if (converter.isPresent()) {
                try {
                    converter.get().attachObject(stack.getChannel(index));
                } catch (OperationFailedException e) {
                    throw new CreateException("Cannot attach the channel to the converter");
                }
            }
        }
    }
    // END: constructors

    // START: factory methods
    public static DisplayStack create(Channel channel) throws CreateException {
        DisplayStack display = new DisplayStack(new Stack(channel));
        try {
            display.addConvertersAsNeeded();
        } catch (SetOperationFailedException e) {
            throw new CreateException(e);
        }
        return display;
    }

    public static DisplayStack create(Stack stack) throws CreateException {
        DisplayStack display = new DisplayStack(stack);
        display.checkNumberChannels(stack.getNumberChannels());
        try {
            display.addConvertersAsNeeded();
        } catch (SetOperationFailedException e) {
            throw new CreateException(e);
        }
        return display;
    }

    public static DisplayStack create(RGBStack rgbStack) throws CreateException {
        DisplayStack display = new DisplayStack(rgbStack.asStack());
        try {
            display.addConvertersAsNeeded();
        } catch (SetOperationFailedException e) {
            throw new CreateException(e);
        }
        return display;
    }

    // END: factory methods

    public long numberNonNullConverters() {
        return listConverters.stream().filter(Optional::isPresent).count();
    }

    public ImageDimensions dimensions() {
        return stack.dimensions();
    }

    public final int getNumberChannels() {
        return stack.getNumberChannels();
    }

    // Only creates a new channel if needs be, otherwise reuses existing channel
    public Channel createChannel(int index, boolean alwaysNew) {

        return mapper.mapChannelIfSupported(
                index,
                (channel, converter) -> converter.convert(channel, conversionPolicy(alwaysNew)),
                Functions.identity());
    }

    // Always creates a new channel
    public Channel createChannelDuplicate(int index) {
        return mapper.mapChannelIfSupported(
                index,
                (channel, converter) -> converter.convert(channel, ConversionPolicy.ALWAYS_NEW),
                Channel::duplicate);
    }

    // Always creates a new channel, but just capturing a portion of the channel
    public Channel createChannelDuplicateForBoundingBox(int index, BoundingBox box) {

        Channel out =
                ChannelFactory.instance()
                        .create(
                                stack.getChannel(index)
                                        .dimensions()
                                        .duplicateChangeExtent(box.extent()),
                                VoxelDataTypeUnsignedByte.INSTANCE);

        mapper.callChannelIfSupported(
                index,
                (channel, convert) ->
                        copyPixelsTo(index, box, out.voxels().asByte(), box.shiftToOrigin()),
                channel ->
                        stack.getChannel(index)
                                .voxels()
                                .copyVoxelsTo(box, out.voxels(), box.shiftToOrigin()));

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
        return deriveStack(index -> createChannel(index, alwaysNew));
    }

    public Stack deriveStackDuplicate() {
        return deriveStack(this::createChannelDuplicate);
    }

    /** Copies pixels on a particular channel to an output buffer */
    public void copyPixelsTo(
            int channelIndex,
            BoundingBox sourceBox,
            Voxels<ByteBuffer> voxelsDestination,
            BoundingBox destinationBox) {

        mapper.callChannelIfSupported(
                channelIndex,
                (channel, converter) -> {
                    BoundingBox allLocalBox = destinationBox.shiftToOrigin();

                    VoxelsWrapper destBoxNonByte =
                            VoxelsFactory.instance()
                                    .create(destinationBox.extent(), channel.getVoxelDataType());
                    channel.voxels().copyVoxelsTo(sourceBox, destBoxNonByte, allLocalBox);

                    Voxels<ByteBuffer> destBoxByte =
                            VoxelsFactory.getByte().createInitialized(destinationBox.extent());
                    converter.getVoxelsConverter().convertFrom(destBoxNonByte, destBoxByte);

                    destBoxByte
                            .extracter()
                            .boxCopyTo(allLocalBox, voxelsDestination, destinationBox);
                },
                channel ->
                        channel.voxels()
                                .asByte()
                                .extracter()
                                .boxCopyTo(sourceBox, voxelsDestination, destinationBox));
    }

    public Optional<VoxelDataType> unconvertedDataType() {
        VoxelDataType dataType = stack.getChannel(0).getVoxelDataType();
        // If they don't all have the same dataType we return null
        if (!stack.allChannelsHaveType(dataType)) {
            return Optional.empty();
        }
        return Optional.of(dataType);
    }

    public int getUnconvertedVoxelAt(int channelIndex, Point3i point) {
        return stack.getChannel(channelIndex).extracter().voxel(point);
    }

    public RegionExtracter createRegionExtracter() {
        return new RegionExtracterFromDisplayStack(listConverters, stack);
    }

    public DisplayStack maxIntensityProjection() throws OperationFailedException {
        try {
            return new DisplayStack(stack.maximumIntensityProjection(), listConverters);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public DisplayStack extractSlice(int z) throws CreateException {
        return new DisplayStack(stack.extractSlice(z), listConverters);
    }

    public BufferedImage createBufferedImage() throws CreateException {
        if (stack.getNumberChannels() == 3) {
            return BufferedImageFactory.createRGB(
                    voxelsForChannel(0),
                    voxelsForChannel(1),
                    voxelsForChannel(2),
                    stack.dimensions().extent());
        }
        return BufferedImageFactory.createGrayscale(voxelsForChannel(0));
    }

    public BufferedImage createBufferedImageBBox(BoundingBox box) throws CreateException {

        if (box.extent().z() != 1) {
            throw new CreateException("BBox must have a single pixel z-height");
        }

        if (stack.getNumberChannels() == 3) {
            return BufferedImageFactory.createRGB(
                    voxelsForChannelBoundingBox(0, box),
                    voxelsForChannelBoundingBox(1, box),
                    voxelsForChannelBoundingBox(2, box),
                    box.extent());
        }
        return BufferedImageFactory.createGrayscale(voxelsForChannelBoundingBox(0, box));
    }

    private Voxels<ByteBuffer> voxelsForChannel(int channelIndex) {
        return mapper.mapChannelIfSupported(
                channelIndex,
                (channel, converter) ->
                        converter
                                .getVoxelsConverter()
                                .convertFrom(channel.voxels(), VoxelsFactory.getByte()),
                channel -> channel.voxels().asByte());
    }

    @SuppressWarnings("unchecked")
    private Voxels<ByteBuffer> voxelsForChannelBoundingBox(int channelIndex, BoundingBox box) {

        Voxels<?> voxelsUnconverted = stack.getChannel(channelIndex).extracter().region(box, true);
        return mapper.mapChannelIfSupported(
                channelIndex,
                (channel, converter) ->
                        converter
                                .getVoxelsConverter()
                                .convertFrom(
                                        new VoxelsWrapper(voxelsUnconverted),
                                        VoxelsFactory.getByte()),
                channel -> (Voxels<ByteBuffer>) voxelsUnconverted);
    }

    private Stack deriveStack(IntFunction<Channel> indexToChannel) {
        Stack out = new Stack();
        for (int index = 0; index < stack.getNumberChannels(); index++) {
            try {
                out.addChannel(indexToChannel.apply(index));
            } catch (IncorrectImageSizeException e) {
                throw new AnchorImpossibleSituationException();
            }
        }
        return out;
    }

    private static ConversionPolicy conversionPolicy(boolean alwaysNew) {
        return alwaysNew ? ConversionPolicy.ALWAYS_NEW : ConversionPolicy.DO_NOT_CHANGE_EXISTING;
    }

    private void addEmptyConverters(int number) {
        for (int c = 0; c < number; c++) {
            listConverters.add(Optional.empty());
        }
    }

    private void setConverterFor(
            int channelIndex, ChnlConverterAttached<Channel, ByteBuffer> converter)
            throws SetOperationFailedException {
        try {
            converter.attachObject(stack.getChannel(channelIndex));
        } catch (OperationFailedException e) {
            throw new SetOperationFailedException(e);
        }
        listConverters.set(channelIndex, Optional.of(converter));
    }

    private void addConvertersAsNeeded() throws SetOperationFailedException {
        addEmptyConverters(getNumberChannels());
        for (int index = 0; index < getNumberChannels(); index++) {
            if (!stack.getChannel(index)
                    .getVoxelDataType()
                    .equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
                setConverterFor(
                        index,
                        new ChnlConverterChnlUpperLowerQuantileIntensity(
                                QUANTILE_LOWER, QUANTILE_UPPER));
            }
        }
    }

    private void checkNumberChannels(int numberChannels) throws CreateException {

        if (numberChannels > 3) {
            throw new CreateException(
                    String.format(
                            "Cannot convert to DisplayStack as there are %d channels. There must be 3 or less.",
                            numberChannels));
        }

        if (numberChannels == 2) {
            try {
                stack.addBlankChannel();
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }
        }
    }

    private ChannelMapper createChannelMapper() {
        return new ChannelMapper(stack::getChannel, listConverters::get);
    }
}
