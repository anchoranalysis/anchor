/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Two {@link VoxelBuffer}s representing the source and destination of a copy operation.
 *
 * <p>The buffers must have:
 *
 * <ul>
 *   <li>An identical number of channels.
 *   <li>or the source must be singled-channeled (which is then used as every channel to match the
 *       destination).
 * </ul>
 *
 * @author Owen Feehan
 */
class BufferPair {

    /** The buffer to copy <b>from</b>. */
    private final VoxelBuffer<?>[] source;

    /** The buffer to copy <b>to</b>. */
    private final VoxelBuffer<?>[] destination;

    /**
     * Create for a particular number of the channels.
     *
     * @param numberChannels the number of channels.
     */
    public BufferPair(int numberChannels) {
        this.source = new VoxelBuffer<?>[numberChannels];
        this.destination = new VoxelBuffer<?>[numberChannels];
    }

    /**
     * Assign buffers for source and destination stacks.
     *
     * @param sourceStack the stack to copy from.
     * @param destinationStack the stack to copy into.
     * @param sourceZ the index of the z-slice to copy from for {@code sourceStack}.
     * @param destinationZ the index of the z-slice to copy into for {@code destinationStack}.
     */
    public void assign(Stack sourceStack, Stack destinationStack, int sourceZ, int destinationZ) {

        // Store the mapping between source and destination channel in an array to quickly access
        // later
        int[] sourceChannelMapping =
                new int[] {
                    selectSourceChannel(0, sourceStack.getNumberChannels()),
                    selectSourceChannel(1, sourceStack.getNumberChannels()),
                    selectSourceChannel(2, sourceStack.getNumberChannels()),
                };

        for (int channelIndex = 0; channelIndex < destination.length; channelIndex++) {

            int destinationChannel = sourceChannelMapping[channelIndex];

            // Load the appropriate channel to copy from, or null to ignore
            source[channelIndex] =
                    destinationChannel != -1
                            ? bufferForSlice(
                                    sourceStack, sourceChannelMapping[channelIndex], sourceZ)
                            : null;
            destination[channelIndex] =
                    bufferForSlice(destinationStack, channelIndex, destinationZ);
        }
    }

    /**
     * Copies a slice from the source stack to the destination.
     *
     * @param cornerMin the minimum corner of the bounding-box to copy (inclusive, z-dimension is
     *     ignored).
     * @param cornerMax the maximum corner of the bounding-box to copy (exclusive, z-dimension is
     *     ignored).
     * @param sizeDestination the size of the destination stack.
     */
    public void copySlice(
            ReadableTuple3i cornerMin, ReadableTuple3i cornerMax, Extent sizeDestination) {

        int index = 0;
        for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
            for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {

                int outPos = sizeDestination.offset(x, y);

                for (int channel = 0; channel < destination.length; channel++) {

                    VoxelBuffer<?> sourceBuffer = source[channel];
                    if (sourceBuffer != null) {
                        destination[channel].putInt(outPos, sourceBuffer.getInt(index));
                    }
                }
                index++;
            }
        }
    }

    /**
     * Selects what channel to use for source for a particular destination {@code channelIndex}.
     *
     * @param destinationChannelIndex which destination channel is being considered. 0 for red, 1
     *     for green, 2 for blue.
     * @param numberChannelsSource how many source channels exist.
     * @return which channel in the source to copy from, or -1 to avoid copying this channel.
     */
    private int selectSourceChannel(int destinationChannelIndex, int numberChannelsSource) {
        if (destinationChannelIndex < numberChannelsSource) {
            // Corresponds exactly to a source channel, so select.
            return destinationChannelIndex;
        } else {
            if (numberChannelsSource == 1) {
                // The source is grayscale, so always take the first channel, so its added as white.
                return 0;
            } else {
                // Insufficient number of channels to do full RGB, so skip any channel in
                // destination
                // that doesn't have a corresponding channel in source.
                return -1;
            }
        }
    }

    private static VoxelBuffer<?> bufferForSlice(Stack stack, int channelIndex, int sliceIndex) {
        return stack.getChannel(channelIndex).voxels().slice(sliceIndex);
    }
}
