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
import java.util.List;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.mpp.index.factory.VoxelPartitionFactory;

/**
 * An index structure that organizes voxel partitions by region and slice.
 *
 * @param <T> The type of the voxel partition
 */
public class IndexByRegion<T> {

    private final List<VoxelPartition<T>> list;

    /**
     * Constructs an IndexByRegion with a specified number of regions and slices.
     *
     * @param factory The factory to create voxel partitions
     * @param numberRegions The number of regions
     * @param numberSlices The number of slices per region
     */
    public IndexByRegion(VoxelPartitionFactory<T> factory, int numberRegions, int numberSlices) {
        list = new ArrayList<>(numberRegions);
        FunctionalIterate.repeat(numberRegions, () -> list.add(factory.create(numberSlices)));
    }

    /**
     * Gets the combined voxel partition for all slices in a region.
     * This method should only be used for read-only operations to maintain integrity with the combined list.
     *
     * @param regionID The ID of the region
     * @return The combined voxel partition for the region
     */
    public T getForAllSlices(int regionID) {
        return list.get(regionID).getCombined();
    }

    /**
     * Gets the voxel partition for a specific slice in a region.
     * This method should only be used for read-only operations to maintain integrity with the combined list.
     *
     * @param regionID The ID of the region
     * @param sliceID The ID of the slice
     * @return The voxel partition for the specified region and slice
     */
    public T getForSlice(int regionID, int sliceID) {
        return list.get(regionID).getSlice(sliceID);
    }

    /**
     * Adds a value to the voxel list for a specific region and slice.
     *
     * @param regionID The ID of the region
     * @param sliceID The ID of the slice
     * @param val The value to add
     */
    public void addToVoxelList(int regionID, int sliceID, int val) {
        list.get(regionID).addForSlice(sliceID, val);
    }

    /**
     * Cleans up the voxel partitions using the provided factory.
     *
     * @param factory The factory to use for cleanup
     */
    public void cleanUp(VoxelPartitionFactory<T> factory) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).cleanUp(factory);
        }
    }

    /**
     * Gets the number of slices in each region.
     *
     * @return The number of slices
     */
    public int numSlices() {
        return list.get(0).numberSlices();
    }
}