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

import java.nio.ByteBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

public final class VoxelBufferByte extends VoxelBuffer<ByteBuffer> {

    private final ByteBuffer delegate;

    private VoxelBufferByte(ByteBuffer delegate) {
        super();
        this.delegate = delegate;
    }

    public static VoxelBufferByte allocate(int size) {
        return new VoxelBufferByte(ByteBuffer.allocate(size));
    }

    public static VoxelBufferByte wrap(byte[] arr) {
        return new VoxelBufferByte(ByteBuffer.wrap(arr));
    }

    public static VoxelBufferByte wrap(ByteBuffer buffer) {
        return new VoxelBufferByte(buffer);
    }

    @Override
    public ByteBuffer buffer() {
        return delegate;
    }

    public byte get() {
        return delegate.get();
    }

    public byte get(int index) {
        return delegate.get(index);
    }

    public ByteBuffer put(int index, byte b) {
        return delegate.put(index, b);
    }

    public final byte[] array() {
        return delegate.array();
    }

    @Override
    public VoxelBuffer<ByteBuffer> duplicate() {
        return new VoxelBufferByte(ByteConverter.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedByteVoxelType.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return ByteConverter.unsignedByteToInt(delegate.get(index));
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
    public void transferFrom(int destIndex, VoxelBuffer<ByteBuffer> src, int srcIndex) {
        delegate.put(destIndex, src.buffer().get(srcIndex));
    }

    @Override
    public int size() {
        return delegate.capacity();
    }
}
