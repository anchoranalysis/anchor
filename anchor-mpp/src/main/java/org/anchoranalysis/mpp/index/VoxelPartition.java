/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.index;

import org.anchoranalysis.mpp.index.factory.VoxelPartitionFactory;

/**
 * A partition of voxel values by slice.
 *
 * @param <T> The type representing the voxel partition
 */
public interface VoxelPartition<T> {

    /**
     * Gets the voxel partition for a specific slice.
     * This method should only be used for read-only operations to maintain integrity with the combined list.
     *
     * @param sliceID The ID of the slice
     * @return The voxel partition for the specified slice
     */
    T getSlice(int sliceID);

    /**
     * Adds a value to the voxel partition for a specific slice.
     *
     * @param sliceID The ID of the slice
     * @param val The value to add
     */
    void addForSlice(int sliceID, int val);

    /**
     * Gets the combined voxel partition for all slices.
     * This method should only be used for read-only operations.
     *
     * @return The combined voxel partition
     */
    T getCombined();

    /**
     * Cleans up the voxel partition using the provided factory.
     *
     * @param factory The factory to use for cleanup
     */
    void cleanUp(VoxelPartitionFactory<T> factory);

    /**
     * Gets the number of slices in the partition.
     *
     * @return The number of slices
     */
    int numberSlices();
}