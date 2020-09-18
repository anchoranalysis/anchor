package org.anchoranalysis.image.voxel.buffer;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creating voxel-buffers and arrays of voxel-buffers of various types.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class VoxelBufferFactory {

    /**
     * Allocates a new <i>unsigned byte</i> voxel-buffer of given size.
     *
     * @param capacity the capacity (size).
     * @return a new {@link VoxelBuffer} with newly allocated (non-direct) memory.
     */
    public static VoxelBuffer<UnsignedByteBuffer> allocateUnsignedByte(int capacity) {
        return new VoxelBufferUnsignedByte(UnsignedByteBuffer.allocate(capacity));
    }
    
    /**
     * Creates an array of <i>unsigned byte</i> voxel-buffers of given size.
     * 
     * @param size the size of the array
     * @return a newly created array (with elements set to null).
     */
    public static VoxelBuffer<UnsignedByteBuffer>[] allocateUnsignedByteArray(int size) {
        return new VoxelBufferUnsignedByte[size];
    }
    
    /**
     * Allocates a new <i>unsigned short</i> voxel-buffers of given size.
     *
     * @param capacity the capacity (size).
     * @return a new {@link VoxelBuffer} with newly allocated (non-direct) memory.
     */
    public static VoxelBuffer<UnsignedShortBuffer> allocateUnsignedShort(int capacity) {
        return new VoxelBufferUnsignedShort(UnsignedShortBuffer.allocate(capacity));
    }
    
    /**
     * Creates an array of <i>unsigned short</i> voxel-buffers of given size.
     * 
     * @param size the size of the array
     * @return a newly created array (with elements set to null).
     */
    public static VoxelBuffer<UnsignedShortBuffer>[] allocateUnsignedShortArray(int size) {
        return new VoxelBufferUnsignedShort[size];
    }
    
    /**
     * Allocates a new <i>unsigned int</i> voxel-buffers of given size.
     *
     * @param capacity the capacity (size).
     * @return a new {@link VoxelBuffer} with newly allocated (non-direct) memory.
     */
    public static VoxelBufferUnsignedInt allocateUnsignedInt(int capacity) {
        return new VoxelBufferUnsignedInt(UnsignedIntBuffer.allocate(capacity));
    }
    
    /**
     * Creates an array of <i>unsigned int</i> voxel-buffers of given size.
     * 
     * @param size the size of the array
     * @return a newly created array (with elements set to null).
     */
    public static VoxelBuffer<UnsignedIntBuffer>[] allocateUnsignedIntArray(int size) {
        return new VoxelBufferUnsignedInt[size];
    }
    
    /**
     * Allocates a new <i>float</i> voxel-buffers of given size.
     *
     * @param capacity the capacity (size).
     * @return a new {@link VoxelBuffer} with newly allocated (non-direct) memory.
     */
    public static VoxelBufferFloat allocateFloat(int capacity) {
        return new VoxelBufferFloat(FloatBuffer.allocate(capacity));
    }
    
    /**
     * Creates an array of <i>float</i> voxel-buffers of given size.
     * 
     * @param size the size of the array
     * @return a newly created array (with elements set to null).
     */
    public static VoxelBuffer<FloatBuffer>[] allocateFloatArray(int size) {
        return new VoxelBufferFloat[size];
    }
}
