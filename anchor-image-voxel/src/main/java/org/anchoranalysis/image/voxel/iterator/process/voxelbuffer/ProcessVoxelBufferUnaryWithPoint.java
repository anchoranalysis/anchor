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
 * Processes a 3D point like {@link ProcessVoxelBufferUnary} but also exposes a {@link Point3i}.
 *
 * @param <T> buffer-type
 * @param <E> exception that may be thrown by the processor.
 * @author Owen Feehan
 */
@FunctionalInterface
public interface ProcessVoxelBufferUnaryWithPoint<T, E extends Exception> {

    /**
     * Processes a voxel location in a buffer.
     *
     * @param point a point with global coordinates
     * @param buffer a buffer for the current slice for which {@code offset} refers to a particular
     *     location
     * @param offset an offset value for the current slice (i.e. indexing XY only, but not Z)
     * @throws E if anything goes wrong
     */
    void process(Point3i point, VoxelBuffer<T> buffer, int offset) throws E;
}
