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

import com.google.common.base.Preconditions;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.slice.SliceBufferIndex;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * A factory for creating voxels with a particular buffer-type
 *
 * <p>This class (and all its sub-classes) are <b>immutable</b>.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
public interface VoxelsFactoryTypeBound<T> {

    Voxels<T> create(SliceBufferIndex<T> pixelsForPlane);

    Voxels<T> createInitialized(Extent extent);

    Voxels<T> createUninitialized(Extent extent);

    VoxelDataType dataType();

    default Voxels<T> createForVoxelBuffer(VoxelBuffer<T> buffer, Extent extent) {
        Preconditions.checkArgument(extent.volumeXY() == buffer.capacity());

        Voxels<T> out = createUninitialized(extent);
        out.replaceSlice(0, buffer);
        return out;
    }

    /**
     * Creates and initializes voxels that correspond to a particular bounding-box region.
     *
     * @param box the bounding-box region
     * @return newly created voxels of the same extent as {@code box} and retaining an association
     *     with {@code box}.
     */
    default BoundedVoxels<T> createBounded(BoundingBox box) {
        return new BoundedVoxels<>(box, createInitialized(box.extent()));
    }
}
