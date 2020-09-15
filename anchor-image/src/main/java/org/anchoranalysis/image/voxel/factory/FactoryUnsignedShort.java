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

package org.anchoranalysis.image.voxel.factory;

import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsAsShort;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.sliceindex.FromShort;
import org.anchoranalysis.image.voxel.sliceindex.SliceBufferIndex;

final class FactoryUnsignedShort implements VoxelsFactoryTypeBound<UnsignedShortBuffer> {

    private static final VoxelDataType DATA_TYPE = UnsignedShortVoxelType.INSTANCE;

    @Override
    public Voxels<UnsignedShortBuffer> create(SliceBufferIndex<UnsignedShortBuffer> pixelsForPlane) {
        return new VoxelsAsShort(pixelsForPlane);
    }

    @Override
    public Voxels<UnsignedShortBuffer> createInitialized(Extent extent) {
        return new VoxelsAsShort(FromShort.createInitialized(extent));
    }

    @Override
    public Voxels<UnsignedShortBuffer> createUninitialized(Extent extent) {
        return new VoxelsAsShort(FromShort.createUninitialized(extent));
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
