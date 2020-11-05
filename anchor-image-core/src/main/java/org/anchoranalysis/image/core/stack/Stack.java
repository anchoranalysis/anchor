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
import lombok.Getter;
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
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.Extent;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * One or more single-channel images that all have the same dimensions.
 *
 * <p>This is one of the fundamental image data structures in Anchor.
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
    @Getter private final boolean rgb;

    /** An internal data-structure where the stacks are stored. */
    private final StackNotUniformSized delegate;

    public Stack(boolean rgb) {
        this.delegate = new StackNotUniformSized();
        this.rgb = rgb;
    }

    public Stack() {
        this(false);
    }

    public Stack(Channel channel) {
        this.delegate = new StackNotUniformSized(channel);
        this.rgb = false;
    }

    public Stack(
            Dimensions dimensions,
            ChannelFactorySingleType factory,
            int numberChannels,
            boolean rgb) {
        this(rgb);
        FunctionalIterate.repeat(
                numberChannels,
                () -> delegate.addChannel(factory.createEmptyInitialised(dimensions)));
    }

    public Stack(boolean rgb, Channel... channels) throws IncorrectImageSizeException {
        this(rgb);
        for (Channel channel : channels) {
            addChannel(channel);
        }
    }

    public Stack(Stream<Channel> channelStream) throws IncorrectImageSizeException {
        this(false, channelStream);
    }

    public Stack(boolean rgb, Stream<Channel> channelStream) throws IncorrectImageSizeException {
        this.delegate = new StackNotUniformSized(channelStream);
        this.rgb = rgb;
        if (!delegate.isUniformlySized()) {
            throw new IncorrectImageSizeException("Channels in streams are not uniformly sized");
        }
    }

    /** Copy constructor (deep-copies channels) */
    private Stack(Stack src) {
        this.delegate = src.delegate.duplicate();
        this.rgb = src.rgb;
    }

    /**
     * Produces a new stack with a particular mapping applied to each channel.
     *
     * <p>The function applied to the channel should ensure it produces uniform sizes.
     *
     * @param mapping performs an operation on a channel and produces a modified channel (or a
     *     different one entirely)
     * @return a new stack (after any modification by {@code mapping}) preserving the channel order
     * @throws OperationFailedException if the channels produced have non-uniform sizes
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
     * channel also available)
     *
     * <p>The function applied to the channel should ensure it produces uniform sizes.
     *
     * @param mapping performs an operation on a channel and produces a modified channel (or a
     *     different one entirely)
     * @return a new stack (after any modification by {@code mapping}) preserving the channel order
     * @throws OperationFailedException if the channels produced have non-uniform sizes
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

    public Stack extractSlice(int z) {
        // We know the sizes will be correct
        return new Stack(rgb, delegate.extractSlice(z));
    }

    /**
     * Maximum intensity projection.
     *
     * @return
     */
    public Stack projectMax() {
        // We know the sizes will be correct
        return new Stack(rgb, delegate.projectMax());
    }

    public void addBlankChannel() throws OperationFailedException {

        if (getNumberChannels() == 0) {
            throw new OperationFailedException(
                    "At least one channel must exist from which to guess dimensions");
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

    public final void addChannelsFrom(Stack stack) throws IncorrectImageSizeException {
        for (int index = 0; index < stack.getNumberChannels(); index++) {
            addChannel(stack.getChannel(index));
        }
    }

    public final Channel getChannel(int index) {
        return delegate.getChannel(index);
    }

    public final int getNumberChannels() {
        return delegate.getNumberChannels();
    }

    public Dimensions dimensions() {
        return delegate.getChannel(0).dimensions();
    }

    public Optional<Resolution> resolution() {
        return dimensions().resolution();
    }

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

    public Stack extractUpToThreeChannels() {
        Stack out = new Stack(rgb);
        int maxNum = Math.min(3, delegate.getNumberChannels());
        for (int i = 0; i < maxNum; i++) {
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

    public List<Channel> asListChannels() {
        ArrayList<Channel> out = new ArrayList<>();
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

        VoxelDataType channelDataType = null;
        for (Channel channel : this) {

            if (channelDataType == null) {
                channelDataType = channel.getVoxelDataType();
            } else {
                if (!channel.getVoxelDataType().equals(channelDataType)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if all channels have a specific data-type.
     *
     * @param channelDataType the specific data-type
     * @return true iff all channels have {@code channelDataType} as their voxel data-type.
     */
    public boolean allChannelsHaveType(VoxelDataType channelDataType) {

        for (Channel channel : this) {
            if (!channel.getVoxelDataType().equals(channelDataType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Stack)) {
            return false;
        }

        return equalsDeep((Stack) obj, true);
    }

    /**
     * Are the two stack equal using a deep voxel by voxel comparison of each channel?
     *
     * @param other the stack to compare with
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

    public void updateResolution(Resolution resolution) {
        for (Channel channel : this) {
            channel.updateResolution(Optional.of(resolution));
        }
    }
}
