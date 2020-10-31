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

import com.google.common.base.Preconditions;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

final class VoxelBufferUnsignedByte extends VoxelBuffer<UnsignedByteBuffer> {

    private final UnsignedByteBuffer delegate;

    /**
     * Create from a {@link VoxelBufferUnsignedByte} without any underlying bytes.
     *
     * @param buffer the buffer
     */
    public VoxelBufferUnsignedByte(UnsignedByteBuffer buffer) {
        Preconditions.checkArgument(buffer.hasArray());
        this.delegate = buffer;
    }

    @Override
    public UnsignedByteBuffer buffer() {
        return delegate;
    }

    public byte get(int index) {
        return delegate.getRaw(index);
    }

    public UnsignedByteBuffer put(int index, byte b) {
        delegate.putRaw(index, b);
        return delegate;
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> duplicate() {
        return new VoxelBufferUnsignedByte(DuplicateBuffer.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedByteVoxelType.INSTANCE;
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
        delegate.putRaw(index, value);
    }

    @Override
    public void transferFrom(
            int destinationIndex, VoxelBuffer<UnsignedByteBuffer> src, int sourceIndex) {
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

    @Override
    public byte[] underlyingBytes() {
        return delegate.array();
    }
}