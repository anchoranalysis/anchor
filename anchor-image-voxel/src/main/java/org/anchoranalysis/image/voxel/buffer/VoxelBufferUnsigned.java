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

import com.google.common.base.Preconditions;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedBuffer;

/**
 * A parent class for {@link VoxelBuffer} that accepts unsigned-buffers and implements some common
 * operations.
 *
 * @author Owen Feehan
 * @param <T> buffer-type for voxels
 */
public abstract class VoxelBufferUnsigned<T extends UnsignedBuffer> extends VoxelBuffer<T> {

    private final T buffer;

    /**
     * Creates to reuse a particular buffer.
     * 
     * @param buffer the buffer to reuse.
     */
    protected VoxelBufferUnsigned(T buffer) {
        Preconditions.checkArgument(buffer.hasArray());
        this.buffer = buffer;
    }

    @Override
    public T buffer() {
        return buffer;
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    @Override
    public void position(int newPosition) {
        buffer.position(newPosition);
    }

    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }
}
