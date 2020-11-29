/*-
 * #%L
 * anchor-image-voxel
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

import org.anchoranalysis.image.voxel.Voxels;

/**
 * A buffer to which slices may be added to form a projection.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
public interface ProjectableBuffer<T> {

    /**
     * Adds a slice to the buffer.
     *
     * @param voxels voxels for the slice.
     */
    void addSlice(VoxelBuffer<T> voxels);

    /**
     * Performs any final operation before turning the projected buffer.
     *
     * <p>This should be called only <i>once</i> per buffer.
     *
     * @return the projected buffer.
     */
    Voxels<T> completeProjection();
}
