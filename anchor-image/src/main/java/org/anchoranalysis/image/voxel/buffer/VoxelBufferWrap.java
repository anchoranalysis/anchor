package org.anchoranalysis.image.voxel.buffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;

/**
 * Wraps arrays and buffers of primitive-types into {@link VoxelBuffer}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelBufferWrap {

    /**
     * Wraps an existing array (encoding unsigned bytes as a signed array) as a voxel-buffer.
     *
     * @param array the array to wrap
     * @return a new {@link VoxelBuffer} reusing the array internally.
     */
    public static VoxelBuffer<UnsignedByteBuffer> unsignedByteArray(byte[] array) {
        return new VoxelBufferUnsignedByte(UnsignedByteBuffer.wrapRaw(array));
    }

    /**
     * Wraps an unsigned-byte buffer into a voxel-buffer.
     *
     * @param buffer the buffer to wrap
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBuffer<UnsignedByteBuffer> unsignedByteBuffer(UnsignedByteBuffer buffer) {
        return new VoxelBufferUnsignedByte(buffer);
    }

    /**
     * Wraps an unsigned-byte buffer (represented by a NIO signed-buffer) into a voxel-buffer.
     *
     * @param buffer the signed-buffer to wrap as unsigned
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBuffer<UnsignedByteBuffer> unsignedByteRaw(ByteBuffer buffer) {
        return unsignedByteBuffer(UnsignedByteBuffer.wrapRaw(buffer));
    }

    /**
     * Wraps an existing array (encoding unsigned shorts as a signed array) as a voxel-buffer.
     *
     * @param array the array to wrap
     * @return a new {@link VoxelBuffer} reusing the array internally.
     */
    public static VoxelBuffer<UnsignedShortBuffer> unsignedShortArray(short[] array) {
        return new VoxelBufferUnsignedShort(UnsignedShortBuffer.wrapRaw(array));
    }

    /**
     * Wraps an unsigned-short buffer into a voxel-buffer.
     *
     * @param buffer the buffer to wrap
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBuffer<UnsignedShortBuffer> unsignedShortBuffer(UnsignedShortBuffer buffer) {
        return new VoxelBufferUnsignedShort(buffer);
    }

    /**
     * Wraps an unsigned-short buffer (represented by a NIO signed-buffer) into a voxel-buffer.
     *
     * @param buffer the signed-buffer to wrap as unsigned
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBuffer<UnsignedShortBuffer> unsignedShortRaw(ShortBuffer buffer) {
        return unsignedShortBuffer(UnsignedShortBuffer.wrapRaw(buffer));
    }

    /**
     * Wraps an unsigned-int buffer into a voxel-buffer.
     *
     * @param array the array to wrap
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBufferUnsignedInt unsignedIntArray(int[] array) {
        return new VoxelBufferUnsignedInt(UnsignedIntBuffer.wrapRaw(array));
    }

    /**
     * Wraps an unsigned-int buffer (represented by a NIO signed-buffer) into a voxel-buffer.
     *
     * @param buffer the signed-buffer to wrap as unsigned
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBufferUnsignedInt unsignedIntBuffer(UnsignedIntBuffer buffer) {
        return new VoxelBufferUnsignedInt(buffer);
    }

    /**
     * Wraps a float buffer into a voxel-buffer.
     *
     * @param buffer the buffer to wrap
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBufferFloat floatBuffer(FloatBuffer buffer) {
        return new VoxelBufferFloat(buffer);
    }

    /**
     * Wraps an float-array into a voxel-buffer.
     *
     * @param array the array to wrap
     * @return a new {@link VoxelBuffer} reusing the buffer internally.
     */
    public static VoxelBufferFloat floatArray(float[] array) {
        return new VoxelBufferFloat(FloatBuffer.wrap(array));
    }
}
