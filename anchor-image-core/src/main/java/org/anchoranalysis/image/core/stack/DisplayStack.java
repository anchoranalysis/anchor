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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.Getter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.ConversionPolicy;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Stack that contains 1 or 3 channels so that we and display it as either grayscale or as an RGB
 * unsigned 8-bit image, respectively.
 *
 * <p>A converter is optionally associated with each channel, used to convert the source images into
 * unsigned 8-bit.
 *
 * <p>When only two channels are present, one particular channel (with index={@value
 * DisplayStack#CHANNEL_TO_SKIP_WHEN_TWO}) is left blank.
 *
 * @author Owen Feehan
 */
public class DisplayStack {

    /**
     * Index of channel to leave blank when there are only two channels. 0=first, 1=second, 2=third.
     */
    public static final int CHANNEL_TO_SKIP_WHEN_TWO = 1;

    /** The underlying stack that will be displayed, possibly after conversion. */
    @Getter private final Stack stack;

    /**
     * A list of optional converters that will be applied to the respective channel in {@code stack}
     * if they exist.
     */
    @Getter
    private final List<Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>>> converters;

    private final ChannelMapper mapper;

    // START: constructors

    /**
     * Create for a particular {@link Stack} where all channels are already guaranteed to be
     * unsigned-bit.
     *
     * @param stack the stack to display.
     */
    public DisplayStack(Stack stack) {
        this.stack = stack.extractUpToThreeChannels();
        this.converters = new ArrayList<>();
        this.mapper = createChannelMapper();
    }

    /**
     * Create for a particular {@link Stack} that may needed to be converted.
     *
     * @param stack the stack to display.
     * @param eventuallyThree when true, the stack will eventually have three channels. when false,
     *     it will have one.
     * @param createConverter creates any necessary converter to map it to unsigned 8 bit.
     * @throws CreateException if unable to convert a {@link Channel}.
     */
    public DisplayStack(
            Stack stack,
            boolean eventuallyThree,
            Function<VoxelDataType, ChannelConverterAttached<Channel, UnsignedByteBuffer>>
                    createConverter)
            throws CreateException {
        this.stack = stack.extractUpToThreeChannels();
        this.converters = new ArrayList<>();
        this.mapper = createChannelMapper();
        try {
            addConvertersAsNeeded(eventuallyThree ? 3 : 1, createConverter);
        } catch (SetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    // We don't need to worry about channel numbers
    private DisplayStack(
            Stack stack,
            List<Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>>> listConverters)
            throws CreateException {
        this.stack = stack;
        this.converters = listConverters;
        this.mapper = createChannelMapper();

        for (int index = 0; index < listConverters.size(); index++) {
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

    /**
     * Does the display-stack contain an RGB image?
     *
     * @return true if the contained image is RGB, false if it is grayscale.
     */
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
     *
     * @return the extent.
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
     * Resolution of voxels to physical measurements.
     *
     * <p>e.g. physical size of each voxel in a particular dimension.
     *
     * @return the resolution.
     */
    public Optional<Resolution> resolution() {
        return stack.resolution();
    }

    /**
     * Create {@link Channel} for a particular {@code index} after applying conversion.
     *
     * @param index the index of the channel in the {@link Stack} to create a converted version of.
     * @param alwaysNew if true, a new channel is always created. Otherwise it is created, only if
     *     needs be.
     * @return the {@link Channel} after extracting from the stack and applying conversion.
     */
    public Channel createChannel(int index, boolean alwaysNew) {
        return mapper.mapChannelIfSupported(
                index,
                (channel, converter) -> converter.convert(channel, conversionPolicy(alwaysNew)),
                Functions.identity());
    }

    /**
     * Creates a new {@link Channel} that refers to only a {@link BoundingBox} portion of the {@link
     * DisplayStack}.
     *
     * <p>Existing voxels are always duplicated, and never reused.
     *
     * @param index the index of the channel.
     * @param box the bounding-box portion to extract.
     * @return a newly created {@link Channel} containing extracted voxels, corresponding to {@code
     *     box}.
     */
    public Channel extractChannelForBoundingBox(int index, BoundingBox box) {

        Dimensions dimensionsBox =
                stack.getChannel(index).dimensions().duplicateChangeExtent(box.extent());
        Channel out =
                ChannelFactory.instance().create(dimensionsBox, UnsignedByteVoxelType.INSTANCE);

        mapper.consumeChannelIfSupported(
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
     *     if needed.
     * @return a stack with either 1 or 3 channels (depending on what's passed into display-stack),
     *     all of which are unsigned 8-bit.
     */
    public Stack deriveStack(boolean alwaysNew) {
        return deriveStack(index -> createChannel(index, alwaysNew));
    }

    /**
     * Copies pixels from a particular channel to an output buffer.
     *
     * @param channelIndex the index of the <i>source</i> channel to copy <b>from</b>.
     * @param sourceBox the bounding-box in the source channel to copy <b>from</b>.
     * @param destinationVoxels where to copy the pixels <b>to</b>.
     * @param destinationBox the bounding-box in the destination channel to copy <b>to</b>.
     */
    public void copyPixelsTo(
            int channelIndex,
            BoundingBox sourceBox,
            Voxels<UnsignedByteBuffer> destinationVoxels,
            BoundingBox destinationBox) {

        mapper.consumeChannelIfSupported(
                channelIndex,
                (channel, converter) -> {
                    BoundingBox allLocalBox = destinationBox.shiftToOrigin();

                    VoxelsUntyped destBoxNonByte =
                            VoxelsFactory.instance()
                                    .createEmpty(
                                            destinationBox.extent(), channel.getVoxelDataType());
                    channel.voxels().copyVoxelsTo(sourceBox, destBoxNonByte, allLocalBox);

                    Voxels<UnsignedByteBuffer> destBoxByte =
                            converter
                                    .getVoxelsConverter()
                                    .convertFrom(destBoxNonByte, VoxelsFactory.getUnsignedByte());
                    destBoxByte.extract().boxCopyTo(allLocalBox, destinationVoxels, destinationBox);
                },
                channel ->
                        channel.voxels()
                                .asByte()
                                .extract()
                                .boxCopyTo(sourceBox, destinationVoxels, destinationBox));
    }

    /**
     * The data-type of the underlying voxels before they are converted to 8-bit.
     *
     * @return the data-type if all channels have identical data-type, or {@link Optional#empty} if
     *     they vary.
     */
    public Optional<VoxelDataType> unconvertedDataType() {
        VoxelDataType dataType = stack.getChannel(0).getVoxelDataType();
        // If they don't all have the same dataType we return Optional#empty
        if (!stack.allChannelsHaveType(dataType)) {
            return Optional.empty();
        }
        return Optional.of(dataType);
    }

    /**
     * Retrieve the intensity of a voxel at a particular point, before any conversion is applied.
     *
     * @param channelIndex the index of the channel in which the voxel resides.
     * @param point the point in the channel corresponding to the voxel.
     * @return the intensity value, before any conversion.
     */
    public int getUnconvertedVoxelAt(int channelIndex, Point3i point) {
        return stack.getChannel(channelIndex).extract().voxel(point);
    }

    /**
     * Creates a <a href="https://en.wikipedia.org/wiki/Maximum_intensity_projection">Maximum
     * Intensity Projection</a> of each channel.
     *
     * <p>Note that if the channels do not need projections, the existing {@link Channel} is reused
     * in the newly created {@link Stack}. But if a projection is needed, it is always freshly
     * created as a new channel.
     *
     * @return a newly created {@link Stack}, with maximum intensity projections of each {@link
     *     Channel}.
     */
    public DisplayStack projectMax() {
        try {
            return new DisplayStack(stack.projectMax(), converters);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Extract a particular z-slice from the {@link DisplayStack} as a new stack, applying any
     * applicable conversion.
     *
     * <p>The existing voxels may be reused, if no conversion needs to be applied.
     *
     * @param z the index in the Z-dimension of the slice to extract.
     * @return the extracted slice, as a new {@link DisplayStack} after any applicable conversion.
     * @throws CreateException if a channel cannot be attached to a converter.
     */
    public DisplayStack extractSlice(int z) throws CreateException {
        return new DisplayStack(stack.extractSlice(z), converters);
    }

    /**
     * Derives a {@link Stack} with one or three 8-bit channels.
     *
     * @param channelToSkipWhenTwo when there are only two channels, this channel is left blank (0
     *     for red, 1 for blue, 2 for green).
     * @return a newly reated {@link Stack}.
     */
    private Stack deriveStack(IntFunction<Channel> indexToChannel) {
        Stack out = new Stack(stack.getNumberChannels() == 3);
        int eventualNumberChannels = stack.getNumberChannels() == 1 ? 1 : 3;
        for (int index = 0; index < eventualNumberChannels; index++) {
            try {
                if (getNumberChannels() == 2 && index == CHANNEL_TO_SKIP_WHEN_TWO) {
                    out.addChannel(
                            ChannelFactory.instance()
                                    .create(stack.dimensions(), UnsignedByteVoxelType.INSTANCE));
                } else {
                    out.addChannel(indexToChannel.apply(translateChannelIndex(index)));
                }
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
            int channelIndex,
            Channel channelSource,
            Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>> converter)
            throws SetOperationFailedException {
        try {
            if (converter.isPresent()) {
                converter.get().attachObject(channelSource);
            }
        } catch (OperationFailedException e) {
            throw new SetOperationFailedException(e);
        }
        converters.set(channelIndex, converter);
    }

    /**
     * Adds as many converters and blank channels as needed so that the total number becomes {@code
     * eventualNumberChannels}.
     *
     * @param eventualNumberChannels how many channels will exist in the stack (after adding any
     *     needed blanks).
     * @param createConverter how to create a converter for any channel that is not already unsigned
     *     8-bit.
     * @throws SetOperationFailedException if unable to convert a particular channel.
     */
    private void addConvertersAsNeeded(
            int eventualNumberChannels,
            Function<VoxelDataType, ChannelConverterAttached<Channel, UnsignedByteBuffer>>
                    createConverter)
            throws SetOperationFailedException {
        addEmptyConverters(eventualNumberChannels);

        // The index we retrieve from in the original channel
        for (int index = 0; index < getNumberChannels(); index++) {
            Channel channelSource = stack.getChannel(index);
            VoxelDataType dataType = channelSource.getVoxelDataType();
            if (!dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
                setConverterFor(index, channelSource, Optional.of(createConverter.apply(dataType)));
            }
        }
    }

    /** Translates a destination channel index into a source index. */
    private int translateChannelIndex(int index) {
        if (getNumberChannels() == 1) {
            // Always 0
            return 0;
        } else if (getNumberChannels() == 2) {
            switch (CHANNEL_TO_SKIP_WHEN_TWO) {
                case 0:
                    return index - 1;
                case 1:
                    if (index == 2) {
                        return 1;
                    } else {
                        return index;
                    }
                default:
                    return index;
            }
            // Take care of the keep free channel
        } else {
            return index;
        }
    }

    private ChannelMapper createChannelMapper() {
        return new ChannelMapper(
                index -> stack.getChannel(translateChannelIndex(index)),
                index -> {
                    if (index < converters.size()) {
                        return converters.get(index);
                    } else {
                        return Optional.empty();
                    }
                });
    }
}
