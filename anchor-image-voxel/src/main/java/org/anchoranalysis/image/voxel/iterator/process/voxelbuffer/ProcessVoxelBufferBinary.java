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

import java.nio.Buffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPoint;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnary;

/**
 * Processes a 3D point like {@link ProcessPoint} but also retrieves <b>two</b> {@link VoxelBuffer}s
 * for the current z-slice.
 *
 * <p>It is very similar to {@link ProcessBufferUnary} but uses {@link VoxelBuffer} instead of a
 * {@link Buffer}.
 *
 * @author Owen Feehan
 * @param <S> first buffer-type
 * @param <T> second buffer-type
 */
@FunctionalInterface
public interface ProcessVoxelBufferBinary<S, T> {

    /**
     * Processes a voxel location in a buffer
     *
     * @param buffer1 first buffer for the current slice for which {@code offset} refers to a
     *     particular location
     * @param buffer2 second buffer for the current slice for which {@code offset} refers to a
     *     particular location
     * @param offset an offset value for the current slice (i.e. indexing XY only, but not Z)
     */
    void process(VoxelBuffer<S> buffer1, VoxelBuffer<T> buffer2, int offset);
}
