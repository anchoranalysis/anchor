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
    private final VoxelBuffer<?>[] source;
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
        for (int channelIndex = 0; channelIndex < destination.length; channelIndex++) {

            if (source.length == 1) {
                sourceZ = 1;
            }

            source[channelIndex] = bufferForSlice(sourceStack, channelIndex, sourceZ);
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

                for (int channel = 0; channel < source.length; channel++) {
                    destination[channel].putInt(outPos, source[channel].getInt(index));
                }
                index++;
            }
        }
    }

    private static VoxelBuffer<?> bufferForSlice(Stack stack, int channelIndex, int sliceIndex) {
        return stack.getChannel(channelIndex).voxels().slice(sliceIndex);
    }
}
