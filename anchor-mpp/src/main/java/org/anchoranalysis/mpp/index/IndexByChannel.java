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

import java.util.ArrayList;
import org.anchoranalysis.mpp.index.factory.VoxelPartitionFactory;

/**
 * An index structure that organizes voxel partitions by channel.
 *
 * @param <T> The type of the voxel partition
 */
public class IndexByChannel<T> {

    private ArrayList<IndexByRegion<T>> delegate = new ArrayList<>();

    /**
     * Adds an IndexByRegion to the channel index.
     *
     * @param e The IndexByRegion to add
     * @return true if the IndexByRegion was successfully added, false otherwise
     */
    public boolean add(IndexByRegion<T> e) {
        return delegate.add(e);
    }

    /**
     * Initializes the channel index with a specified number of channels, regions, and slices.
     *
     * @param factory The factory to create voxel partitions
     * @param numChannel The number of channels
     * @param numRegions The number of regions per channel
     * @param numSlices The number of slices per region
     */
    public void initialize(
            VoxelPartitionFactory<T> factory, int numChannel, int numRegions, int numSlices) {

        for (int i = 0; i < numChannel; i++) {
            delegate.add(new IndexByRegion<>(factory, numRegions, numSlices));
        }
    }

    /**
     * Gets the IndexByRegion for a specific channel.
     *
     * @param index The index of the channel
     * @return The IndexByRegion for the specified channel
     */
    public IndexByRegion<T> get(int index) {
        return delegate.get(index);
    }

    /**
     * Gets the number of channels in the index.
     *
     * @return The number of channels
     */
    public int size() {
        return delegate.size();
    }

    /**
     * Cleans up the voxel partitions for all channels using the provided factory.
     *
     * @param factory The factory to use for cleanup
     */
    public void cleanUp(VoxelPartitionFactory<T> factory) {
        for (int i = 0; i < delegate.size(); i++) {
            delegate.get(i).cleanUp(factory);
        }
    }
}
