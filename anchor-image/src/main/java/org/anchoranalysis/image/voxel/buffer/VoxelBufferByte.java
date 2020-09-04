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

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.nio.ByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class VoxelBufferByte extends VoxelBuffer<UnsignedByteBuffer> {

    private final UnsignedByteBuffer delegate;

    public static VoxelBufferByte allocate(int size) {
        return new VoxelBufferByte(UnsignedByteBuffer.allocate(size));
    }

    public static VoxelBufferByte wrapArray(byte[] arr) {
        return new VoxelBufferByte(UnsignedByteBuffer.wrap(arr));
    }

    public static VoxelBuffer<UnsignedByteBuffer> wrapBuffer(UnsignedByteBuffer buffer) {
        return new VoxelBufferByte(buffer);
    }
    
    public static VoxelBuffer<UnsignedByteBuffer> wrapUnsigned(ByteBuffer buffer) {
        return wrapBuffer( new UnsignedByteBuffer(buffer) );
    }

    @Override
    public UnsignedByteBuffer buffer() {
        return delegate;
    }

    public byte get(int index) {
        return delegate.get(index);
    }

    public UnsignedByteBuffer put(int index, byte b) {
        delegate.put(index, b);
        return delegate;
    }

    public final byte[] array() {
        return delegate.array();
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> duplicate() {
        return new VoxelBufferByte(DuplicateBuffer.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedByteVoxelType.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return delegate.getInt(index);
    }

    @Override
    public void putInt(int index, int val) {
        delegate.put(index, (byte) val);
    }

    @Override
    public void putByte(int index, byte val) {
        delegate.put(index, val);
    }

    @Override
    public void transferFrom(int destIndex, VoxelBuffer<UnsignedByteBuffer> src, int srcIndex) {
        delegate.put(destIndex, src.buffer().get(srcIndex));
    }

    @Override
    public int size() {
        return delegate.capacity();
    }
}
