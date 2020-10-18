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

/**
 * Processes <b>two</b> buffers for each z-slice jointly, without any offset or point.
 *
 * <p>This is intended for usage with the relative {@code get} and {@code #put} methods of buffer
 * classes.
 *
 * @param <S> type of first buffer
 * @param <T> type of second buffer
 * @author Owen Feehan
 */
@FunctionalInterface
public interface ProcessBufferBinaryWithoutOffset<S, T> {

    /** Notifies the processor that there has been a change in slice (z global coordinate) */
    default void notifyChangeSlice(int z) {}

    /**
     * Processes a voxel location in a buffer
     *
     * @param buffer1 first buffer for the current slice
     * @param buffer2 second buffer for the current slice
     */
    void process(S buffer1, T buffer2);
}
