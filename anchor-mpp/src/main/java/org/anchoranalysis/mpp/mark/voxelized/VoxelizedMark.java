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

package org.anchoranalysis.mpp.mark.voxelized;

import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * A voxelized representation of a Mark i.e. a mark turned into voxels.
 *
 * @author Owen Feehan
 */
public interface VoxelizedMark {

    /**
     * Gets the voxel representation of the mark.
     *
     * @return the {@link BoundedVoxels} representing the mark
     */
    BoundedVoxels<UnsignedByteBuffer> voxels();

    /**
     * Gets the maximum intensity projection of the voxelized mark.
     *
     * @return the {@link BoundedVoxels} representing the maximum intensity projection
     */
    BoundedVoxels<UnsignedByteBuffer> voxelsMaximumIntensityProjection();

    /**
     * Gets the bounding-box enclosing the voxelized representation of the mark.
     *
     * @return the {@link BoundingBox} enclosing the mark
     */
    BoundingBox boundingBox();

    /**
     * Gets the bounding-box flattened in z dimension.
     *
     * @return the flattened {@link BoundingBox}
     */
    BoundingBox boundingBoxFlattened();

    /**
     * Creates a duplicate of this voxelized mark.
     *
     * @return a new {@link VoxelizedMark} instance that is a copy of this one
     */
    VoxelizedMark duplicate();

    /**
     * Calculates statistics for all slices in a specific channel and region.
     *
     * @param channelID the ID of the channel
     * @param regionID the ID of the region
     * @return the {@link VoxelStatistics} for the specified channel and region
     */
    VoxelStatistics statisticsForAllSlices(int channelID, int regionID);

    /**
     * Calculates statistics for all slices in a specific channel and region, using a mask from another channel.
     *
     * @param channelID the ID of the channel for statistics
     * @param regionID the ID of the region
     * @param maskChannelID the ID of the channel to use as a mask
     * @return the {@link VoxelStatistics} for the specified channel and region, masked by another channel
     */
    VoxelStatistics statisticsForAllSlicesMaskSlice(int channelID, int regionID, int maskChannelID);

    /**
     * Calculates statistics for a specific slice in a specific channel and region.
     *
     * @param channelID the ID of the channel
     * @param regionID the ID of the region
     * @param sliceID the ID of the slice
     * @return the {@link VoxelStatistics} for the specified channel, region, and slice
     */
    VoxelStatistics statisticsFor(int channelID, int regionID, int sliceID);

    /**
     * Cleans up any resources associated with this voxelized mark.
     */
    void cleanUp();
}