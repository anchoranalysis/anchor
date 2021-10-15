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

import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

final class VoxelBufferUnsignedByte extends VoxelBufferUnsigned<UnsignedByteBuffer> {

    /**
     * Create from a {@link VoxelBufferUnsignedByte} without any underlying bytes.
     *
     * @param buffer the buffer
     */
    public VoxelBufferUnsignedByte(UnsignedByteBuffer buffer) {
        super(buffer);
    }

    public byte get(int index) {
        return buffer().getRaw(index);
    }

    public UnsignedByteBuffer put(int index, byte b) {
        buffer().putRaw(index, b);
        return buffer();
    }

    @Override
    public VoxelBuffer<UnsignedByteBuffer> duplicate() {
        return new VoxelBufferUnsignedByte(DuplicateBuffer.copy(buffer()));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedByteVoxelType.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return buffer().getUnsigned(index);
    }

    @Override
    public void putInt(int index, int value) {
        buffer().putUnsigned(index, value);
    }

    @Override
    public void putByte(int index, byte value) {
        buffer().putRaw(index, value);
    }

    @Override
    public void copyVoxelFrom(
            int destinationIndex, VoxelBuffer<UnsignedByteBuffer> src, int sourceIndex) {
        buffer().putRaw(destinationIndex, src.buffer().getRaw(sourceIndex));
    }

    @Override
    public byte[] underlyingBytes() {
        return buffer().array();
    }
}
