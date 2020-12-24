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
import org.anchoranalysis.image.voxel.buffer.primitive.PrimitiveConverter;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

final class VoxelBufferUnsignedShort extends VoxelBufferUnsigned<UnsignedShortBuffer> {

    /**
     * Create from a {@link UnsignedShortBuffer} without any underlying bytes.
     *
     * @param buffer the buffer
     */
    public VoxelBufferUnsignedShort(UnsignedShortBuffer buffer) {
        super(buffer);
    }

    @Override
    public VoxelBuffer<UnsignedShortBuffer> duplicate() {
        return new VoxelBufferUnsignedShort(DuplicateBuffer.copy(buffer()));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedShortVoxelType.INSTANCE;
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
        buffer().putUnsigned(index, PrimitiveConverter.unsignedByteToInt(value));
    }

    @Override
    public void transferFrom(
            int destinationIndex, VoxelBuffer<UnsignedShortBuffer> src, int sourceIndex) {
        buffer().putRaw(destinationIndex, src.buffer().getRaw(sourceIndex));
    }

    @Override
    public byte[] underlyingBytes() {
        ByteBuffer bufferSigned = ByteBuffer.allocate(capacity() * 2);
        bufferSigned.asShortBuffer().put(buffer().getDelegate());
        return bufferSigned.array();
    }
}
