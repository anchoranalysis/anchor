/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;

public final class VoxelBufferFloat extends VoxelBuffer<FloatBuffer> {

    private final FloatBuffer delegate;

    private VoxelBufferFloat(FloatBuffer delegate) {
        super();
        this.delegate = delegate;
    }

    public static VoxelBufferFloat wrap(float[] arr) {
        return new VoxelBufferFloat(FloatBuffer.wrap(arr));
    }

    public static VoxelBufferFloat wrap(FloatBuffer buffer) {
        return new VoxelBufferFloat(buffer);
    }

    @Override
    public FloatBuffer buffer() {
        return delegate;
    }

    @Override
    public VoxelBuffer<FloatBuffer> duplicate() {
        return new VoxelBufferFloat(ByteConverter.copy(delegate));
    }

    @Override
    public VoxelDataType dataType() {
        return VoxelDataTypeFloat.INSTANCE;
    }

    public static VoxelBufferFloat allocate(int size) {
        return new VoxelBufferFloat(FloatBuffer.allocate(size));
    }

    @Override
    public int getInt(int index) {
        return (int) delegate.get(index);
    }

    @Override
    public void putInt(int index, int val) {
        delegate.put(index, (float) val);
    }

    @Override
    public void putByte(int index, byte val) {
        delegate.put(index, (float) ByteConverter.unsignedByteToInt(val));
    }

    @Override
    public void transferFrom(int destIndex, VoxelBuffer<FloatBuffer> src, int srcIndex) {
        delegate.put(destIndex, src.buffer().get(srcIndex));
    }

    @Override
    public int size() {
        return delegate.capacity();
    }
}
