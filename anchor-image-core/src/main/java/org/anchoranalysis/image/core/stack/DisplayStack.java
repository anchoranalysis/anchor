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

import com.google.common.base.Functions;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import lombok.Getter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.core.bufferedimage.BufferedImageFactory;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.ConversionPolicy;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.core.channel.convert.attached.channel.UpperLowerQuantileIntensity;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Stack that contains 1 or 3 channels so that we and display it as either or as an RGB unsigned 8-bit
 * image.
 *
 * <p>A converter is optionally associated with each channel, used to convert the source images into
 * unsigned 8-bit.
 *
 * @author Owen Feehan
 */
public class DisplayStack {

    private static final double QUANTILE_LOWER = 0.0001;
    private static final double QUANTILE_UPPER = 0.9999;

    /** The underlying stack that will be displayed, possibly after conversion. */
    @Getter private final Stack stack;

    /** 
     * A list of optional converters that will be applied to the respective channel in {@code stack} if they exist.
     */
    @Getter
    private final List<Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>>> converters;

    private final ChannelMapper mapper;

    // START: constructors
    private DisplayStack(Stack stack) {
        this.stack = stack;
        this.converters = new ArrayList<>();
        this.mapper = createChannelMapper();
    }

    // We don't need to worry about channel numbers
    private DisplayStack(
            Stack stack,
            List<Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>>> listConverters)
            throws CreateException {
        this.stack = stack;
        this.converters = listConverters;
        this.mapper = createChannelMapper();

        for (int index = 0; index < stack.getNumberChannels(); index++) {
            Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>> converter =
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
        return converters.stream().filter(Optional::isPresent).count();
    }

    public boolean isRGB() {
        return stack.getNumberChannels() == 3;
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
     * The width and height and depth of all channels in the {@link Stack}.
     * 
     * <p>i.e. the size of each of the three possible dimensions.
     */
    public Extent extent() {
        return dimensions().extent();
    }

    /**
     * The number of channels in the stack.
     * 
     * @return the number of channels.
     */
    public final int getNumberChannels() {
        return stack.getNumberChannels();
    }
    
    /**
     * Create {@link Channel} for a particular {@code index} after applying conversion.
     *  
     * @param index the index of the channel in the {@link Stack} to create a converted version of.
     * @param alwaysNew if true, a new channel is always created. Otherwise it is created, only if needs be.
     * @return the {@link Channel} after extracting from the stack and applying conversion.
     */
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
                                UnsignedByteVoxelType.INSTANCE);

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
     * @param alwaysNew iff true channels are always created new during conversion, otherwise only
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
            Voxels<UnsignedByteBuffer> voxelsDestination,
            BoundingBox destinationBox) {

        mapper.callChannelIfSupported(
                channelIndex,
                (channel, converter) -> {
                    BoundingBox allLocalBox = destinationBox.shiftToOrigin();

                    VoxelsWrapper destBoxNonByte =
                            VoxelsFactory.instance()
                                    .create(destinationBox.extent(), channel.getVoxelDataType());
                    channel.voxels().copyVoxelsTo(sourceBox, destBoxNonByte, allLocalBox);

                    Voxels<UnsignedByteBuffer> destBoxByte =
                            converter
                                    .getVoxelsConverter()
                                    .convertFrom(destBoxNonByte, VoxelsFactory.getUnsignedByte());
                    destBoxByte.extract().boxCopyTo(allLocalBox, voxelsDestination, destinationBox);
                },
                channel ->
                        channel.voxels()
                                .asByte()
                                .extract()
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
        return stack.getChannel(channelIndex).extract().voxel(point);
    }

    /**
     * Maximum-intensity projection.
     *
     * @return
     */
    public DisplayStack projectMax() {
        try {
            return new DisplayStack(stack.projectMax(), converters);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    public DisplayStack extractSlice(int z) throws CreateException {
        return new DisplayStack(stack.extractSlice(z), converters);
    }

    public BufferedImage createBufferedImage() throws CreateException {
        if (stack.getNumberChannels() == 3) {
            return BufferedImageFactory.createRGB(
                    voxelsForChannel(0), voxelsForChannel(1), voxelsForChannel(2), stack.extent());
        }
        return BufferedImageFactory.createGrayscaleByte(voxelsForChannel(0));
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
        return BufferedImageFactory.createGrayscaleByte(voxelsForChannelBoundingBox(0, box));
    }

    private Voxels<UnsignedByteBuffer> voxelsForChannel(int channelIndex) {
        return mapper.mapChannelIfSupported(
                channelIndex,
                (channel, converter) ->
                        converter
                                .getVoxelsConverter()
                                .convertFrom(channel.voxels(), VoxelsFactory.getUnsignedByte()),
                channel -> channel.voxels().asByte());
    }

    @SuppressWarnings("unchecked")
    private Voxels<UnsignedByteBuffer> voxelsForChannelBoundingBox(
            int channelIndex, BoundingBox box) {

        Voxels<?> voxelsUnconverted = stack.getChannel(channelIndex).extract().region(box, true);
        return mapper.mapChannelIfSupported(
                channelIndex,
                (channel, converter) ->
                        converter
                                .getVoxelsConverter()
                                .convertFrom(
                                        new VoxelsWrapper(voxelsUnconverted),
                                        VoxelsFactory.getUnsignedByte()),
                channel -> (Voxels<UnsignedByteBuffer>) voxelsUnconverted);
    }

    private Stack deriveStack(IntFunction<Channel> indexToChannel) {
        Stack out = new Stack(stack.getNumberChannels() == 3);
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
            converters.add(Optional.empty());
        }
    }

    private void setConverterFor(
            int channelIndex, ChannelConverterAttached<Channel, UnsignedByteBuffer> converter)
            throws SetOperationFailedException {
        try {
            converter.attachObject(stack.getChannel(channelIndex));
        } catch (OperationFailedException e) {
            throw new SetOperationFailedException(e);
        }
        converters.set(channelIndex, Optional.of(converter));
    }

    private void addConvertersAsNeeded() throws SetOperationFailedException {
        addEmptyConverters(getNumberChannels());
        for (int index = 0; index < getNumberChannels(); index++) {
            if (!stack.getChannel(index)
                    .getVoxelDataType()
                    .equals(UnsignedByteVoxelType.INSTANCE)) {
                setConverterFor(
                        index, new UpperLowerQuantileIntensity(QUANTILE_LOWER, QUANTILE_UPPER));
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
        return new ChannelMapper(stack::getChannel, converters::get);
    }

    public Optional<Resolution> resolution() {
        return stack.resolution();
    }
}
