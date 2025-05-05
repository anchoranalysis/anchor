/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.projection;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.convert.VoxelsConverterMulti;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A projectable buffer that also keeps track of the number of voxels in it.
 *
 * @param <T> type of buffer used, both as input and result, of the projection
 */
public abstract class CountedProjectableBuffer<T> implements ProjectableBuffer<T> {

    /** Converter for voxel types. */
    private static final VoxelsConverterMulti CONVERTER = new VoxelsConverterMulti();

    /** The voxel data-type to use for the flattened buffer. */
    private final VoxelsFactoryTypeBound<T> flatType;

    /** Accumulates the sum of voxel values. */
    protected final Voxels<FloatBuffer> voxelsSum;

    /** The count of voxels added. */
    private int count = 0;

    /**
     * Creates with minimal parameters, as no preprocessing is necessary.
     *
     * @param flatType the voxel data-type to use for the flattened buffer.
     * @param extent the size expected for images that will be projected.
     */
    protected CountedProjectableBuffer(VoxelsFactoryTypeBound<T> flatType, Extent extent) {
        this.voxelsSum = VoxelsFactory.getFloat().createInitialized(extent);
        this.flatType = flatType;
    }

    @Override
    public void addVoxelBuffer(VoxelBuffer<T> voxelBuffer) {
        addVoxelBufferInternal(voxelBuffer, 0);
        count++;
    }

    @Override
    public void addVoxels(Voxels<T> voxels) {
        for (int z = 0; z < voxels.extent().z(); z++) {
            addVoxelBufferInternal(voxels.slice(z), z);
        }
        count++;
    }

    /**
     * Adds a {@link VoxelBuffer} without incrementing the count.
     *
     * @param voxelBuffer the voxels to add.
     * @param z the index (beginning at 0) of the z-slice that the voxels are from.
     */
    protected abstract void addVoxelBufferInternal(VoxelBuffer<T> voxelBuffer, int z);

    /**
     * Flattens the accumulated voxels to the target type.
     *
     * @param voxels the voxels to flatten
     * @return the flattened {@link Voxels} of type T
     */
    protected Voxels<T> flattenFrom(Voxels<FloatBuffer> voxels) {
        return CONVERTER.convert(new VoxelsUntyped(voxels), flatType);
    }

    /**
     * Divides all voxel values by the count of added voxels.
     *
     * @param voxels the voxels to divide
     */
    protected void divideVoxelsByCount(Voxels<FloatBuffer> voxels) {
        voxels.arithmetic().divideBy(count);
    }
}
