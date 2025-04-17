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

package org.anchoranalysis.mpp.index.factory;

import org.anchoranalysis.mpp.index.VoxelPartition;

/**
 * A factory for creating {@link VoxelPartition} instances.
 *
 * @param <T> the type of parts stored in the voxel partition
 */
public interface VoxelPartitionFactory<T> {

    /**
     * Creates a new {@link VoxelPartition} with the specified number of slices.
     *
     * @param numSlices the number of slices in the voxel partition
     * @return a new {@link VoxelPartition} instance
     */
    VoxelPartition<T> create(int numSlices);

    /**
     * Adds an unused part to the factory.
     *
     * <p>This method is typically used to recycle parts that are no longer needed, allowing them to
     * be reused in future voxel partitions.
     *
     * @param part the unused part to add
     */
    void addUnused(T part);
}
