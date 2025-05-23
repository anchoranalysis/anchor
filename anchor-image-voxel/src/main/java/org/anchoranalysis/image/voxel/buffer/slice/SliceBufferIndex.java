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

package org.anchoranalysis.image.voxel.buffer.slice;

import java.util.function.Consumer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferUnary;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A container with voxel-buffers for each z-slice.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
public interface SliceBufferIndex<T> {

    /**
     * A {@link VoxelBuffer} corresponding to a particular z-slice.
     *
     * @param z the index (beginning at 0) of all z-slices.
     * @return the corresponding buffer for {@code z}.
     */
    VoxelBuffer<T> slice(int z);

    /**
     * The underlying buffer corresponding to a particular z-slice.
     *
     * @param z the index (beginning at 0) of all z-slices.
     * @return the corresponding buffer for {@code z}.
     */
    default T sliceBuffer(int z) {
        return slice(z).buffer();
    }

    /**
     * Replaces the voxels for a particular z-slice.
     *
     * @param z the index of z-slice to replace.
     * @param sliceToAssign the voxels for the new slice to assign.
     */
    void replaceSlice(int z, VoxelBuffer<T> sliceToAssign);

    /**
     * The size of each buffer.
     *
     * @return the size.
     */
    Extent extent();

    /**
     * Calls {@code sliceConsumer} once for each slice with the respective buffer.
     *
     * <p>This occurs sequentially from 0 (inclusive) to {@code z()} (exclusive).
     *
     * @param sliceConsumer called for each index (z-value).
     */
    default void iterateOverSlices(Consumer<VoxelBuffer<T>> sliceConsumer) {
        int zMax = extent().z();
        for (int z = 0; z < zMax; z++) {
            sliceConsumer.accept(slice(z));
        }
    }

    /**
     * Calls {@code process} for each offset in each slice.
     *
     * <p>This occurs sequentially from 0 (inclusive) to {@code extent.z()} (exclusive) and from 0
     * (inclusive) to {@code extent.x() * extent.y()} (exclusive) for the offsets.
     *
     * @param process called for each offset on each slice.
     */
    default void iterateOverSlicesAndOffsets(ProcessVoxelBufferUnary<T> process) {
        iterateOverSlices(
                buffer -> extent().iterateOverXYOffset(offset -> process.process(buffer, offset)));
    }
}
