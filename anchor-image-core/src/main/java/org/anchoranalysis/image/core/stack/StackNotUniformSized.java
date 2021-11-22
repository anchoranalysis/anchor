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
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * A list of {@link Channel}s that are permitted to vary in size.
 *
 * <p>This is unlike a {@link Stack} which mandates that all have identical size.
 *
 * @author Owen Feehan
 */
public class StackNotUniformSized implements Iterable<Channel> {

    /** The channels in the stack. */
    private final List<Channel> channels;

    /** Creates an empty with no channels. */
    public StackNotUniformSized() {
        channels = new ArrayList<>();
    }

    /**
     * Creates from a stream of {@link Channel}s.
     *
     * @param channelStream the stream of channels
     */
    public StackNotUniformSized(Stream<Channel> channelStream) {
        channels = channelStream.collect(Collectors.toList());
    }

    /**
     * Creates from a single {@link Channel}.
     *
     * @param channel the channel
     */
    public StackNotUniformSized(Channel channel) {
        this();
        addChannel(channel);
    }

    /**
     * Creates a new {@link StackNotUniformSized} containing only one particular slice for each
     * respective channel.
     *
     * <p>The existing {@link Voxels} are reused, without creating new buffers.
     *
     * @param sliceIndex the index of the slice to extract (index in z-dimension).
     * @return a newly created {@link StackNotUniformSized} consisting of the slice at {@code
     *     sliceIndex} only, for each respective channel.
     */
    public StackNotUniformSized extractSlice(int sliceIndex) {
        return deriveMapped(channel -> channel.extractSlice(sliceIndex));
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
    public StackNotUniformSized projectMax() {
        return deriveMapped(Channel::projectMax);
    }

    /**
     * Appends a channel to the stack, as the new final-most channel positionwise.
     *
     * @param channel the channel.
     */
    public final void addChannel(Channel channel) {
        channels.add(channel);
    }

    /**
     * Returns the channel at a particular position in the stack.
     *
     * @param index the index (zero-indexed).
     * @return the respective channel.
     * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index
     *     &gt;= size()</tt>)
     */
    public final Channel getChannel(int index) {
        return channels.get(index);
    }

    /**
     * The number of channels in the {@link Stack}.
     *
     * @return the number of channels.
     */
    public final int getNumberChannels() {
        return channels.size();
    }

    /**
     * Do all channels have identical dimensions?
     *
     * @return true if the {@link Dimensions} of all channels are identical.
     */
    public boolean isUniformlySized() {

        if (channels.size() <= 1) {
            return true;
        }

        Dimensions dimensions = channels.get(0).dimensions();

        for (int c = 1; c < channels.size(); c++) {

            if (!dimensions.equals(channels.get(c).dimensions())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Do all {@link Channel}s in the stack have identical voxel data-type?
     *
     * @return true if all have identical type, false if any one differs.
     */
    public boolean isUniformTyped() {

        if (channels.size() <= 1) {
            return true;
        }

        VoxelDataType dataType = channels.get(0).getVoxelDataType();

        for (int c = 1; c < channels.size(); c++) {

            if (!dataType.equals(channels.get(c).getVoxelDataType())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<Channel> iterator() {
        return channels.iterator();
    }

    /**
     * A deep-copy.
     *
     * @return newly created deep-copy.
     */
    public StackNotUniformSized duplicate() {
        return deriveMapped(Channel::duplicate);
    }

    /**
     * Derives a new {@link StackNotUniformSized} with an operator applies to the existing {@link
     * Channel}s.
     */
    private StackNotUniformSized deriveMapped(UnaryOperator<Channel> operator) {
        StackNotUniformSized out = new StackNotUniformSized();
        for (Channel channel : this) {
            out.addChannel(operator.apply(channel));
        }
        return out;
    }
}
