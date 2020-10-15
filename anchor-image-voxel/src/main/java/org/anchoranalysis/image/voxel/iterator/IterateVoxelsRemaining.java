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
