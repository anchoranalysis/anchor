package org.anchoranalysis.image.voxel.buffer;

import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedBuffer;
import com.google.common.base.Preconditions;

/**
 * A parent class for {@link VoxelBuffer} that accepts unsigned-buffers and implements some common operations.
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type for voxels
 */
public abstract class VoxelBufferUnsigned<T extends UnsignedBuffer> extends VoxelBuffer<T> {

    private final T buffer;
    
    protected VoxelBufferUnsigned(T buffer) {
        Preconditions.checkArgument(buffer.hasArray());
        this.buffer = buffer;
    }
    
    @Override
    public T buffer() {
        return buffer;
    }
    
    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    @Override
    public void position(int newPosition) {
        buffer.position(newPosition);
    }

    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }
}
