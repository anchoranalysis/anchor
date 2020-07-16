/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public final class VoxelBufferShort extends VoxelBuffer<ShortBuffer> {

    private final ShortBuffer delegate;

    public VoxelBufferShort(ShortBuffer delegate) {
        super();
        this.delegate = delegate;
    }

    public static VoxelBufferShort allocate(int size) {
        return new VoxelBufferShort(ShortBuffer.allocate(size));
    }

    public static VoxelBufferShort wrap(short[] arr) {
        return new VoxelBufferShort(ShortBuffer.wrap(arr));
    }

    public static VoxelBufferShort wrap(ShortBuffer buffer) {
        return new VoxelBufferShort(buffer);
    }

    @Override
    public ShortBuffer buffer() {
        return delegate;
    }

    @Override
    public VoxelBuffer<ShortBuffer> duplicate() {
        return new VoxelBufferShort(ByteConverter.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return VoxelDataTypeUnsignedShort.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return ByteConverter.unsignedShortToInt(delegate.get(index));
    }

    @Override
    public void putInt(int index, int val) {
        delegate.put(index, (short) val);
    }

    @Override
    public void putByte(int index, byte val) {
        delegate.put(index, (short) ByteConverter.unsignedByteToInt(val));
    }

    @Override
    public void transferFrom(int destIndex, VoxelBuffer<ShortBuffer> src, int srcIndex) {
        delegate.put(destIndex, src.buffer().get(srcIndex));
    }

    @Override
    public int size() {
        return delegate.capacity();
    }
}
