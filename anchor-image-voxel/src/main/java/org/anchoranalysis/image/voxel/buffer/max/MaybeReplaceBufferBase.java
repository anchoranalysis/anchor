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

package org.anchoranalysis.image.voxel.buffer.max;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.spatial.box.Extent;

/**
 * The buffer used when making a <a
 * href="https://en.wikipedia.org/wiki/Maximum_intensity_projection">Maximum Intensity
 * Projection</a> or similarly a min-intensity-projection.
 *
 * <p>The operating of possibly replacing an existing voxel with another is generalized to be
 * applicable to both.
 *
 * @author Owen Feehan
 * @param <T> type of buffer used, both as input and result, of the maximum intesnity projection
 */
abstract class MaybeReplaceBufferBase<T> implements ProjectableBuffer<T> {

    /**
     * Target buffer, where the current state of the projection is stored.
     *
     * <p>We delay making the projection until the first slice is known.
     */
    private Voxels<T> projection;

    private final VoxelsFactoryTypeBound<T> factory;
    private final Extent extent;

    protected MaybeReplaceBufferBase(Extent extent, VoxelsFactoryTypeBound<T> factory) {
        this.factory = factory;
        this.extent = extent.flattenZ();
    }

    @Override
    public void addVoxels(Voxels<T> voxels) {
        for (int z = 0; z < voxels.extent().z(); z++) {
            addVoxelBuffer(voxels.slice(z));
        }
    }

    @Override
    public void addVoxelBuffer(VoxelBuffer<T> voxels) {

        T buffer = voxels.buffer();

        if (projection != null) {

            VoxelBuffer<T> projectionBuffer = projection.slice(0);

            // Maybe replace the existing voxels
            while (projectionBuffer.hasRemaining()) {
                maybeReplaceCurrentBufferPosition(buffer, projectionBuffer.buffer());
            }
        } else {
            projection = factory.createInitialized(extent);
            VoxelBuffer<T> projectionBuffer = projection.slice(0);

            // Copy the existing voxels as its the first buffer
            while (projectionBuffer.hasRemaining()) {
                assignCurrentBufferPosition(buffer, projectionBuffer.buffer());
            }
        }
    }

    @Override
    public Voxels<T> completeProjection() {
        return projection;
    }

    /**
     * Maybe perform a replacement on the current {@code buffer} position of {@code projection} with
     * the current buffer position of {@code projection}.
     *
     * <p>It expects both buffers {@code get} methods to be called once, so that they both advance.
     *
     * @param buffer the buffer being iterated over for the slice that is being added
     * @param projection the buffer being iterated over for the projection voxels.
     */
    protected abstract void maybeReplaceCurrentBufferPosition(T buffer, T projection);

    /**
     * Aassigns the current {@code buffer} position to the current {@code projection} position.
     *
     * <p>It expects both buffers {@code get} methods to be called once, so that they both advance.
     *
     * @param buffer the buffer being iterated over for the slice that is being added
     * @param projection the buffer being iterated over for the projection voxels.
     */
    protected abstract void assignCurrentBufferPosition(T buffer, T projection);
}
