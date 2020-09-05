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

package org.anchoranalysis.image.voxel.buffer;

import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.PrimitiveConverter;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class VoxelBufferUnsignedShort extends VoxelBuffer<UnsignedShortBuffer> {

    private final UnsignedShortBuffer delegate;

    /**
     * Allocates a new voxel-buffer of given size.
     * 
     * @param capacity the capacity (size).
     * @return a new {@link VoxelBuffer} with newly allocated (non-direct) memory.
     */
    public static VoxelBuffer<UnsignedShortBuffer> allocate(int capacity) {
        return new VoxelBufferUnsignedShort(UnsignedShortBuffer.allocate(capacity));
    }

    /**
     * Wraps an existing array (encoding unsigned shorts as a signed array) as a voxel-buffer.
     * 
     * @param array the array to wrap
     * @return a new {@link VoxelBuffer} reusing the array internally.
     */
    public static VoxelBuffer<UnsignedShortBuffer> wrapArray(short[] array) {
        return new VoxelBufferUnsignedShort(UnsignedShortBuffer.wrapRaw(array));
    }

    /**
     * Wraps an unsigned-buffer into a voxel-buffer.
     * 
     * @param buffer the buffer to wrap
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBuffer<UnsignedShortBuffer> wrapBuffer(UnsignedShortBuffer buffer) {
        return new VoxelBufferUnsignedShort(buffer);
    }
    
    /**
     * Wraps an unsigned-buffer (represented by a NIO signed-buffer) into a voxel-buffer.
     * 
     * @param buffer the signed-buffer to wrap as unsigned
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBuffer<UnsignedShortBuffer> wrapRaw(ShortBuffer buffer) {
        return wrapBuffer( UnsignedShortBuffer.wrapRaw(buffer) );
    }
    
    @Override
    public UnsignedShortBuffer buffer() {
        return delegate;
    }

    @Override
    public VoxelBuffer<UnsignedShortBuffer> duplicate() {
        return new VoxelBufferUnsignedShort(DuplicateBuffer.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedShortVoxelType.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return delegate.getUnsigned(index);
    }

    @Override
    public void putInt(int index, int value) {
        delegate.putUnsigned(index, value);
    }

    @Override
    public void putByte(int index, byte value) {
        delegate.putUnsigned(index, PrimitiveConverter.unsignedByteToInt(value));
    }

    @Override
    public void transferFrom(int destinationIndex, VoxelBuffer<UnsignedShortBuffer> src, int sourceIndex) {
        delegate.putRaw(destinationIndex, src.buffer().getRaw(sourceIndex));
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }
    
    @Override
    public boolean hasRemaining() {
        return delegate.hasRemaining();
    }
    
    @Override
    public void position(int newPosition) {
        delegate.position(newPosition);
    }
    
    @Override
    public boolean isDirect() {
        return delegate.isDirect();
    }
}
