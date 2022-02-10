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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.core.functional.checked.CheckedBiFunction;
import org.anchoranalysis.core.functional.checked.CheckedUnaryOperator;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * One or more single-channel images that all have the same dimensions.
 *
 * <p>This is one of the fundamental image data structures in Anchor.
 *
 * <p>The contained channels have a particular voxel-type, but this is deliberately not exposed as a
 * type-parameter to {@link Stack} as data-structure, relying on the user to remain aware i.e. it is
 * weakly-typed.
 *
 * <p>A {@link Channel} (or any underlying voxel-buffer} should never exist more than once in a
 * {@link Stack}. It is assumed that each {@link Channel} has independent buffers, and if this
 * assumption is violated, iteration can not always proceed correctly.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Stack implements Iterable<Channel> {

    /**
     * If true, and the stack has exactly three channels, this stack can be interpreted as a RGB
     * image.
     *
     * <p>This is an important flag for determining how a stack is displayed visually, determining
     * whether a stack is portrayed as a color image or composite grayscale channels.
     */
    private final boolean rgb;

    /** An internal data-structure where the stacks are stored. */
    private final StackNotUniformSized delegate;

    /**
     * Creates a new empty {@link Stack} that will not become an RGB image after adding channels.
     */
    public Stack() {
        this(false);
    }

    /**
     * Creates a new empty {@link Stack} and whether it will become an RGB image or not.
     *
     * @param rgb whether the stack will represent an RGB image after adding channels.
     */
    public Stack(boolean rgb) {
        this.delegate = new StackNotUniformSized();
        this.rgb = rgb;
    }

    /**
     * Creates a {@link Stack} with a single channel.
     *
     * @param channel the channel.
     */
    public Stack(Channel channel) {
        this.delegate = new StackNotUniformSized(channel);
        this.rgb = false;
    }

    /**
     * Creates a {@link Stack} with a single <i>unsigned-byte</i> channel of size {@code channel}.
     *
     * @param extent the size of the channel to create.
     */
    public Stack(Extent extent) {
        this(
                ChannelFactory.instance()
                        .create(new Dimensions(extent), UnsignedByteVoxelType.INSTANCE));
    }

    /**
     * Create with a particular number of empty {@link Channel}s.
     *
     * @param dimensions the dimensions to use for all channels.
     * @param factory a factory to create the empty channels.
     * @param numberChannels how many channels to create.
     * @param rgb whether the channels are RGB (in which case, {@code numberChannels} should be 3).
     * @throws CreateException if {@code rgb==true} and there are not three channels.
     */
    public Stack(
            Dimensions dimensions,
            ChannelFactorySingleType factory,
            int numberChannels,
            boolean rgb)
            throws CreateException {
        this(rgb);
        if (rgb && numberChannels != 3) {
            throw rgbIncorrectNumberChannelsException(numberChannels);
        }
        FunctionalIterate.repeat(
                numberChannels,
                () -> delegate.addChannel(factory.createEmptyInitialised(dimensions)));
    }

    /**
     * Creates from a varying number of channels, and a flag to indicate if they represent an RGB
     * image or not.
     *
     * @param rgb true, if the channels represent an RGB image, and are in the corresponding order.
     * @param channels the channels.
     * @throws IncorrectImageSizeException if the channels are not of uniform size.
     * @throws CreateException if {@code rgb==true} and there are not three channels.
     */
    public Stack(boolean rgb, Channel... channels)
            throws IncorrectImageSizeException, CreateException { // NOSONAR
        this(rgb);
        if (rgb && channels.length != 3) {
            throw rgbIncorrectNumberChannelsException(channels.length);
        }
        for (Channel channel : channels) {
            addChannel(channel);
        }
    }

    /**
     * Create a {@link Stack} from a stream of {@link Channel}s.
     *
     * <p>It is assumed these channels will <b>not</b> represent an RGB image.
     *
     * @param stream the stream of channels.
     * @throws IncorrectImageSizeException if the channels are not of uniform size.
     */
    public Stack(Stream<Channel> stream) throws IncorrectImageSizeException {
        this(false, stream);
    }

    /**
     * Like {@link #Stack(Stream)} but allows explicitly setting whether it should be interpreted as
     * RGB or not.
     *
     * @param rgb whether to interpret the stream as RGB or not, when it is three channels.
     * @param stream the stream of channels.
     * @throws IncorrectImageSizeException if the channels are not of uniform size.
     */
    public Stack(boolean rgb, Stream<Channel> stream) throws IncorrectImageSizeException {
        this.delegate = new StackNotUniformSized(stream);
        this.rgb = rgb;
        if (!delegate.isUniformlySized()) {
            throw new IncorrectImageSizeException("Channels in streams are not uniformly sized");
        }
    }

    /** Copy constructor (deep-copies channels) */
    private Stack(Stack source) {
        this.delegate = source.delegate.duplicate();
        this.rgb = source.rgb;
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
    public Stack mapChannel(CheckedUnaryOperator<Channel, OperationFailedException> mapping)
            throws OperationFailedException {
        Stack out = new Stack(rgb);
        for (Channel channel : this) {
            try {
                out.addChannel(mapping.apply(channel));
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    /**
     * Produces a new stack with a particular mapping applied to each channel (with an index of the
     * channel also available).
     *
     * <p>The function applied to the channel should ensure it produces uniform sizes.
     *
     * @param mapping performs an operation on a channel and produces a modified channel (or a
     *     different one entirely).
     * @return a new stack (after any modification by {@code mapping}) preserving the channel order.
     * @throws OperationFailedException if the channels produced have non-uniform sizes.
     */
    public Stack mapChannelWithIndex(
            CheckedBiFunction<Channel, Integer, Channel, OperationFailedException> mapping)
            throws OperationFailedException {
        Stack out = new Stack(rgb);
        for (int index = 0; index < getNumberChannels(); index++) {
            Channel channel = getChannel(index);
            try {
                out.addChannel(mapping.apply(channel, index));
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    /**
     * Extract a particular z-slice from the {@link Stack} as a new stack.
     *
     * @param z the index in the Z-dimension of the slice to extract.
     * @return the extracted slice, as a new {@link Stack} but reusing the existing voxels.
     */
    public Stack extractSlice(int z) {
        return new Stack(rgb, delegate.extractSlice(z));
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
     *     Channel} if 3D. Otherwise if 2D, then {@code this} is returned.
     */
    public Stack projectMax() {
        if (delegate.isAnyChannel3D()) {
            return new Stack(rgb, delegate.projectMax());
        } else {
            return this;
        }
    }

    /**
     * Adds a new empty {@link Channel} in the final-most position in the list.
     *
     * <p>The dimensions and type of the new channel are inferred from existing channels.
     *
     * @throws OperationFailedException if no existing channel exists, or the existing channels lack
     *     uniform size or type.
     */
    public void addBlankChannel() throws OperationFailedException {

        if (getNumberChannels() == 0) {
            throw new OperationFailedException(
                    "At least one channel must exist from which to guess dimensions.");
        }

        if (!delegate.isUniformlySized()) {
            throw new OperationFailedException(
                    "Other channels do not have the same dimensions. Cannot make a good guess of dimensions.");
        }

        if (!delegate.isUniformTyped()) {
            throw new OperationFailedException("Other channels do not have the same type.");
        }

        Channel first = getChannel(0);
        delegate.addChannel(
                ChannelFactory.instance().create(first.dimensions(), first.getVoxelDataType()));
    }

    /**
     * Appends a channel to the stack, as the new final-most channel position-wise.
     *
     * @param channel the channel.
     * @throws IncorrectImageSizeException if {@code channel} has a mismatching size.
     */
    public final void addChannel(Channel channel) throws IncorrectImageSizeException {

        // We ensure that this channel has the same size as the first
        if (delegate.getNumberChannels() >= 1
                && !channel.dimensions()
                        .extent()
                        .equals(delegate.getChannel(0).dimensions().extent())) {
            throw new IncorrectImageSizeException(
                    "Dimensions of channel do not match existing channel");
        }

        delegate.addChannel(channel);
    }

    /**
     * Add the channels from another instance into this instance.
     *
     * @param stack the stack whose {@link Channel}s will be added to this instance.
     * @throws IncorrectImageSizeException if any channel to be added has a mismatching size.
     */
    public final void addChannelsFrom(Stack stack) throws IncorrectImageSizeException {
        for (int index = 0; index < stack.getNumberChannels(); index++) {
            addChannel(stack.getChannel(index));
        }
    }

    /**
     * Returns the channel at a particular position in the stack.
     *
     * @param index the index (zero-indexed).
     * @return the respective channel.
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >=
     *     size()})
     */
    public final Channel getChannel(int index) {
        return delegate.getChannel(index);
    }

    /**
     * The number of channels in the stack.
     *
     * @return the number of channels.
     */
    public final int getNumberChannels() {
        return delegate.getNumberChannels();
    }

    /**
     * The dimensions of all channels in the stack.
     *
     * @return the dimensions.
     */
    public Dimensions dimensions() {
        return delegate.getChannel(0).dimensions();
    }

    /**
     * Resolution of voxels to physical measurements.
     *
     * <p>e.g. physical size of each voxel in a particular dimension.
     *
     * @return the resolution.
     */
    public Optional<Resolution> resolution() {
        return dimensions().resolution();
    }

    /**
     * The width and height and depth of the image.
     *
     * <p>i.e. the size of each of the three possible dimensions.
     *
     * @return the extent.
     */
    public Extent extent() {
        return dimensions().extent();
    }

    /**
     * Performs a deep copy of the stack, so that all channels are duplicated.
     *
     * @return a new stack with deep-copied channels.
     */
    public Stack duplicateDeep() {
        return new Stack(this);
    }

    /**
     * Is at least one channel 3D?
     *
     * @return true if at least one channel exists with a z-size that is more than one. false
     *     otherwise (including if no channels exist).
     */
    public boolean isAnyChannel3D() {
        return delegate.isAnyChannel3D();
    }

    /**
     * Performs a shallow copy of the stack, so that all channels are reused.
     *
     * @return a new stack with reused channels.
     */
    public Stack duplicateShallow() {
        Stack out = new Stack(rgb);

        try {
            for (int index = 0; index < getNumberChannels(); index++) {
                out.addChannel(getChannel(index));
            }
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }

        return out;
    }

    /**
     * Extracts the first three {@link Channel}s as a new {@link Stack}.
     *
     * <p>If fewer {@link Channel}s exist, only these are included, without throwing an exception.
     *
     * @return either the existing {@link Stack} (if three channels or less) or a newly created
     *     {@link Stack}, reusing the existing {@link Channel}s in this instance.
     */
    public Stack extractUpToThreeChannels() {
        if (getNumberChannels() <= 3) {
            return this;
        }

        Stack out = new Stack(rgb);
        int maxNumber = Math.min(3, delegate.getNumberChannels());
        for (int i = 0; i < maxNumber; i++) {
            try {
                out.addChannel(delegate.getChannel(i));
            } catch (IncorrectImageSizeException e) {
                throw new AnchorImpossibleSituationException();
            }
        }
        return out;
    }

    /**
     * Does the stack have more than one slice in the z-dimension?
     *
     * @return true if there is more than one z slice, false if there is 1
     */
    public boolean hasMoreThanOneSlice() {
        return delegate.getChannel(0).dimensions().z() > 1;
    }

    @Override
    public Iterator<Channel> iterator() {
        return delegate.iterator();
    }

    /**
     * Derives a {@link List} of {@link Channel}s from those in the {@link Stack}, preserving order.
     *
     * @return a newly created {@link List}, reusing the existing {@link Channel}s.
     */
    public List<Channel> asListChannels() {
        List<Channel> out = new ArrayList<>(delegate.getNumberChannels());
        for (Channel channel : delegate) {
            out.add(channel);
        }
        return out;
    }

    /**
     * Determines if all channels have an identical voxel data-type.
     *
     * @return true iff all channels have an identical voxel data-type.
     */
    public boolean allChannelsHaveIdenticalType() {
        if (getNumberChannels() <= 1) {
            return true;
        }

        VoxelDataType dataType = null;
        for (Channel channel : this) {

            if (dataType == null) {
                dataType = channel.getVoxelDataType();
            } else {
                if (!channel.getVoxelDataType().equals(dataType)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if all channels have a specific data-type.
     *
     * @param voxelDataType the specific data-type.
     * @return true iff all channels have {@code voxelDataType} as their voxel data-type.
     */
    public boolean allChannelsHaveType(VoxelDataType voxelDataType) {

        for (Channel channel : this) {
            if (!channel.getVoxelDataType().equals(voxelDataType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (!(other instanceof Stack)) {
            return false;
        }

        return equalsDeep((Stack) other, true);
    }

    /**
     * Are the two stack equal using a deep voxel by voxel comparison of each channel?
     *
     * @param other the stack to compare with.
     * @param compareResolution if true, the image-resolution is also compared for each channel.
     * @return true if they are deemed equals, false otherwise.
     */
    public boolean equalsDeep(Stack other, boolean compareResolution) {

        if (getNumberChannels() != other.getNumberChannels()) {
            return false;
        }

        if (rgb != other.rgb) {
            return false;
        }

        for (int i = 0; i < getNumberChannels(); i++) {
            if (!getChannel(i).equalsDeep(other.getChannel(i), compareResolution)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder().append(getNumberChannels());

        for (Channel channel : this) {
            builder.append(channel);
        }

        return builder.toHashCode();
    }

    /**
     * Assigns a new resolution.
     *
     * <p>This is a <i>mutable</i> operation that replaces existing state.
     *
     * @param resolution the resolution to assign.
     */
    public void assignResolution(Resolution resolution) {
        for (Channel channel : this) {
            channel.assignResolution(Optional.of(resolution));
        }
    }

    /**
     * If true, and the stack has exactly three channels, this stack can be interpreted as a RGB
     * image.
     *
     * <p>This is an important flag for determining how a stack is displayed visually, determining
     * whether a stack is portrayed as a color image or composite grayscale channels.
     *
     * @return whether the stack can be interpreted as an RGB image when it has three channels.
     */
    public boolean isRGB() {
        return rgb;
    }

    /** An exception for when the RGB flag is true, but the number of channels it not three. */
    private static CreateException rgbIncorrectNumberChannelsException(int numberChannels) {
        return new CreateException(
                String.format(
                        "RGB is specified, but there are %d channels instead of 3",
                        numberChannels));
    }
}
