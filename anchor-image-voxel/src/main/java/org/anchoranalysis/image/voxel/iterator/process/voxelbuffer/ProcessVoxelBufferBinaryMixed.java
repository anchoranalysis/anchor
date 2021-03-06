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

package org.anchoranalysis.image.voxel.iterator.process.voxelbuffer;

import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Processes a 3D point like {@link ProcessVoxelBufferBinary} but rather has one {@link VoxelBuffer}
 * and one buffer, and also exposes a {@link Point3i}.
 *
 * @author Owen Feehan
 * @param <S> first buffer-type
 * @param <T> second buffer-type
 */
@FunctionalInterface
public interface ProcessVoxelBufferBinaryMixed<S, T> {

    /**
     * Processes a voxel location in a buffer
     *
     * @param point a point with global coordinates
     * @param buffer1 first {@link VoxelBuffer} for the current slice for which {@code offset}
     *     refers to a particular location
     * @param buffer2 second buffer for the current slice for which {@code offset} refers to a
     *     particular location
     * @param offset an offset value for the current slice (i.e. indexing XY only, but not Z)
     */
    void process(Point3i point, VoxelBuffer<S> buffer1, T buffer2, int offset);
}
