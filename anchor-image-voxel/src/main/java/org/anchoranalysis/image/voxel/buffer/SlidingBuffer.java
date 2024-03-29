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
import org.anchoranalysis.spatial.box.Extent;

/**
 * An index that places a particular z-slice in local context.
 *
 * <p>It can <i>slide</i> along the z-dimension of {@link Voxels} tracking the current z-slice's
 * buffer and neighboring buffers.
 *
 * <p>Specifically, three buffers are always tracked:
 *
 * <ol>
 *   <li>the buffer for the current slice.
 *   <li>the current slice minus 1.
 *   <li>the current slice plus 1.
 * </ol>
 *
 * <p>Can then be shifted (incremented) across all z-slices.
 *
 * <p>The {@link #shift} or {@link #seek} operations can be used to shift the buffer to focus on a
 * particular z-slice.
 *
 * @param <T> buffer-type.
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

    /**
     * Create for particular voxels.
     *
     * @param voxels the voxels to slide a buffer across the z-dimension.
     */
    public SlidingBuffer(Voxels<T> voxels) {
        this.voxels = voxels;
        seek(0); // We start off on slice 0 always
    }

    /**
     * Moves buffer to a particular z-slice.
     *
     * @param sliceIndexToSeek the index of the z-slice to move to.
     */
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

    /** Increments the slice number by one. */
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

    /**
     * Returns the corresponding buffer at a relative z-slice index to the current focused z-slice.
     *
     * @param relativeIndex an index relative to the current focus's z-slice e.g. -1 or 0 or 1
     * @return the corresponding buffer.
     */
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

    /**
     * The size of the voxels across three dimensions.
     *
     * <p>Note this is not the size of an individual z-slice, but rather the size of all voxels.
     *
     * @return the size.
     */
    public Extent extent() {
        return voxels.extent();
    }
}
