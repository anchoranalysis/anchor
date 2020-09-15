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

package org.anchoranalysis.image.voxel;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.sliceindex.SliceBufferIndex;

/**
 * Exposes a subset of z-slices as if they were independently an image.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@AllArgsConstructor
class SubrangeVoxelAccess<T> implements SliceBufferIndex<T> {

    private int zRelative;
    private Extent extent;
    private BoundedVoxels<T> voxels;

    @Override
    public void replaceSlice(int sliceIndex, VoxelBuffer<T> pixels) {
        voxels.replaceSlice(sliceIndex + zRelative, pixels);
    }

    @Override
    public VoxelBuffer<T> slice(int sliceIndex) {
        return voxels.voxels().slice(sliceIndex + zRelative);
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
