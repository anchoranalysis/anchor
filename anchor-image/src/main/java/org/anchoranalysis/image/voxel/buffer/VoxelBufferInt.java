/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer;

import java.nio.IntBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;

public final class VoxelBufferInt extends VoxelBuffer<IntBuffer> {

    private final IntBuffer delegate;

    public VoxelBufferInt(IntBuffer delegate) {
        super();
        this.delegate = delegate;
    }

    public static VoxelBufferInt allocate(int size) {
        return new VoxelBufferInt(IntBuffer.allocate(size));
    }

    public static VoxelBufferInt wrap(int[] arr) {
        return new VoxelBufferInt(IntBuffer.wrap(arr));
    }

    public static VoxelBufferInt wrap(IntBuffer buffer) {
        return new VoxelBufferInt(buffer);
    }

    @Override
    public IntBuffer buffer() {
        return delegate;
    }

    @Override
    public VoxelBuffer<IntBuffer> duplicate() {
        return new VoxelBufferInt(ByteConverter.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return VoxelDataTypeUnsignedInt.INSTANCE;
    }

    @Override
    public int getInt(int index) {
        return ByteConverter.unsignedIntToInt(delegate.get(index));
    }

    @Override
    public void putInt(int index, int val) {
        delegate.put(index, val);
    }

    @Override
    public void putByte(int index, byte val) {
        delegate.put(index, ByteConverter.unsignedByteToInt(val));
    }

    @Override
    public void transferFrom(int destIndex, VoxelBuffer<IntBuffer> src, int srcIndex) {
        delegate.put(destIndex, src.buffer().get(srcIndex));
    }

    @Override
    public int size() {
        return delegate.capacity();
    }
}
