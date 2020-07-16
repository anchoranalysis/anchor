/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

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
        return VoxelDataTypeUnsignedByte.INSTANCE;
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
