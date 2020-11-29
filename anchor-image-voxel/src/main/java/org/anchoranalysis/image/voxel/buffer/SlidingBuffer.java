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

package org.anchoranalysis.image.voxel.buffer;

import lombok.Getter;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.spatial.Extent;

/**
 * Contains the buffer for the current slice, the current slice minus 1, and the current slice plus
 * 1.
 *
 * <p>Can then be shifted (incremented) across all z-slices.
 *
 * @param <T> buffer-type
 */
public final class SlidingBuffer<T> {

    /** The voxels from which buffers corresponding to slices are extracted. */
    @Getter private final Voxels<T> voxels;

    /** The buffer for the currently selected slice. */
    @Getter private VoxelBuffer<T> center;

    /**
     * The voxel-buffer for the slice with the currently selected slice's index {@code +1} or null
     * if it's the final slice.
     */
    @Getter private VoxelBuffer<T> plusOne;

    /**
     * The voxel-buffer for the slice with the currently selected slice's index {@code -1} or null
     * if it's the first slice.
     */
    @Getter private VoxelBuffer<T> minusOne;

    /** The index of the currently selected slice. */
    private int sliceIndex = -1;

    public SlidingBuffer(Voxels<T> voxels) {
        super();
        this.voxels = voxels;
        seek(0); // We start off on slice 0 always
    }

    /** Seeks a particular slice */
    public void seek(int sliceIndexToSeek) {

        if (sliceIndexToSeek == sliceIndex) {
            return;
        }

        sliceIndex = sliceIndexToSeek;
        minusOne = null;
        center = voxels.slice(sliceIndex);

        if ((sliceIndex - 1) >= 0) {
            minusOne = voxels.slice(sliceIndex - 1);
        }

        if ((sliceIndex + 1) < voxels.extent().z()) {
            plusOne = voxels.slice(sliceIndex + 1);
        }
    }

    /** Increments the slice number by one */
    public void shift() {
        minusOne = center;
        center = plusOne;

        sliceIndex++;

        if ((sliceIndex + 1) < voxels.extent().z()) {
            plusOne = voxels.slice(sliceIndex + 1);
        } else {
            plusOne = null;
        }
    }

    public VoxelBuffer<T> bufferRelative(int relativeIndex) {
        switch (relativeIndex) {
            case 1:
                return plusOne;
            case 0:
                return center;
            case -1:
                return minusOne;
            default:
                return voxels.slice(sliceIndex + relativeIndex);
        }
    }

    public Extent extent() {
        return voxels.extent();
    }
}
