package org.anchoranalysis.image.voxel.iterator;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinaryWithoutOffset;

/**
 * Utilities for iterating over <i>remaining</i> voxels in one or more {@link VoxelBuffer}s.
 *
 * <p>A processor is called on each remaining voxel.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsRemaining {

    /**
     * Iterate over remaining voxels each voxel - with <b>two</b> associated <b>buffers</b> for each
     * slice.
     *
     * <p>Precondition: that both buffer's current position are jointly set to a position, with an
     * identical number of remaining voxels, otherwise an exception is thrown e.g. two identically
     * sized buffers with offset=0.
     *
     * @param voxelBuffer1 first voxel-buffer
     * @param voxelBuffer2 second voxel-buffer
     * @param process is called for each voxel in the buffer
     * @param <S> buffer-type for {@code voxels1}
     * @param <T> buffer-type for {@code voxels2}
     */
    public static <S, T> void withTwoBuffersWithoutOffset(
            VoxelBuffer<S> voxelBuffer1,
            VoxelBuffer<T> voxelBuffer2,
            ProcessBufferBinaryWithoutOffset<S, T> process) {

        S buffer1 = voxelBuffer1.buffer();
        T buffer2 = voxelBuffer2.buffer();

        while (voxelBuffer1.hasRemaining()) {
            process.process(buffer1, buffer2);
        }

        Preconditions.checkArgument(!voxelBuffer2.hasRemaining());
    }
}
