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

import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.PrimitiveConverter;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class VoxelBufferShort extends VoxelBuffer<ShortBuffer> {

    private final ShortBuffer delegate;

    public static VoxelBufferShort allocate(int size) {
        return new VoxelBufferShort(ShortBuffer.allocate(size));
    }

    public static VoxelBufferShort wrapArray(short[] arr) {
        return new VoxelBufferShort(ShortBuffer.wrap(arr));
    }

    public static VoxelBuffer<ShortBuffer> wrapBuffer(ShortBuffer buffer) {
        return new VoxelBufferShort(buffer);
    }

    @Override
    public ShortBuffer buffer() {
        return delegate;
    }

    @Override
    public VoxelBuffer<ShortBuffer> duplicate() {
        return new VoxelBufferShort(DuplicateBuffer.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return UnsignedShortVoxelType.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return PrimitiveConverter.unsignedShortToInt(delegate.get(index));
    }

    @Override
    public void putInt(int index, int value) {
        delegate.put(index, (short) value);
    }

    @Override
    public void putByte(int index, byte value) {
        delegate.put(index, (short) PrimitiveConverter.unsignedByteToInt(value));
    }

    @Override
    public void transferFrom(int destinationIndex, VoxelBuffer<ShortBuffer> src, int sourceIndex) {
        delegate.put(destinationIndex, src.buffer().get(sourceIndex));
    }

    @Override
    public int size() {
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
