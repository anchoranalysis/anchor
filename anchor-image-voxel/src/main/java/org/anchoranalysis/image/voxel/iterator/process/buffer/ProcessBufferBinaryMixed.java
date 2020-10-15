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

package org.anchoranalysis.image.voxel.iterator.process.buffer;

import java.nio.Buffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPoint;

/**
 * Processes a 3D point like {@link ProcessPoint} but also retrieves both <b>one</b> {@link VoxelBuffer} for
 * the current z-slice and <b>one</b> {@link Buffer} for the current z-slice.
 *
 * <p>It is very similar to {@link ProcessBufferUnary} but uses two {@link Buffer} of the same type
 * instead of a single one.
 *
 * @param <S> type of <i>first</i> buffer
 * @param <T> type of <i>second</i> buffer
 * @author Owen Feehan
 */
@FunctionalInterface
public interface ProcessBufferBinaryMixed<S,T> {

    /** Notifies the processor that there has been a change in slice (z global coordinate) */
    default void notifyChangeSlice(int z) {}

    /**
     * Processes a voxel location in two buffers.
     *
     * @param point a point with global coordinates
     * @param buffer1 first buffer for the current slice for which {@code offsetSlice} refers to a
     *     particular location
     * @param buffer2 second buffer for the current slice for which {@code offsetSlice} refers to a
     *     particular location
     * @param offset1 an offset value for the current slice for <b>buffer1</b> (i.e. indexing XY
     *     only, but not Z)
     * @param offset2 an offset value for the current slice for <b>buffer2</b> (i.e. indexing XY
     *     only, but not Z)
     */
    void process(Point3i point, VoxelBuffer<S> buffer1, T buffer2, int offset1, int offset2);
}
