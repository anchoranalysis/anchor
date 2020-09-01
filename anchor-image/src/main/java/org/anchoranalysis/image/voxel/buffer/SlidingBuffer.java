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

import java.nio.Buffer;
import lombok.Getter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;

/**
 * Contains the buffer for the current slice, the current slice minus 1, and the current slice plus
 * 1
 *
 * <p>Can then be shifted (incremented) across all z-slices
 *
 * @param <T> buffer-type
 */
public final class SlidingBuffer<T extends Buffer> {

    @Getter private final Voxels<T> voxels;

    @Getter private VoxelBuffer<T> center;

    @Getter private VoxelBuffer<T> plusOne;

    @Getter private VoxelBuffer<T> minusOne;

    private int sliceNumber = -1;

    public SlidingBuffer(Voxels<T> voxels) {
        super();
        this.voxels = voxels;
        seek(0); // We start off on slice 0 always
    }

    /** Seeks a particular slice */
    public void seek(int sliceIndexToSeek) {

        if (sliceIndexToSeek == sliceNumber) {
            return;
        }

        sliceNumber = sliceIndexToSeek;
        minusOne = null;
        center = voxels.slice(sliceNumber);

        if ((sliceNumber - 1) >= 0) {
            minusOne = voxels.slice(sliceNumber - 1);
        }

        if ((sliceNumber + 1) < voxels.extent().z()) {
            plusOne = voxels.slice(sliceNumber + 1);
        }
    }

    /** Increments the slice number by one */
    public void shift() {
        minusOne = center;
        center = plusOne;

        sliceNumber++;

        if ((sliceNumber + 1) < voxels.extent().z()) {
            plusOne = voxels.slice(sliceNumber + 1);
        } else {
            plusOne = null;
        }
    }

    public VoxelBuffer<T> bufferRel(int rel) {
        switch (rel) {
            case 1:
                return plusOne;
            case 0:
                return center;
            case -1:
                return minusOne;
            default:
                return voxels.slice(sliceNumber + rel);
        }
    }

    public Extent extent() {
        return voxels.extent();
    }
}
