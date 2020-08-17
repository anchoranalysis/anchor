package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.function.IntFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsArithmeticFactory {

    /**
     * Create voxels-arithmethic for {@link ByteBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return
     */
    public static VoxelsArithmetic createByte(
            Extent extent, IntFunction<ByteBuffer> bufferForSlice) {
        return new ByteImplementation(extent, bufferForSlice);
    }

    /**
     * Create voxels-arithmethic for {@link ShortBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return
     */
    public static VoxelsArithmetic createShort(
            Extent extent, IntFunction<ShortBuffer> bufferForSlice) {
        return new ShortImplementation(extent, bufferForSlice);
    }

    /**
     * Create voxels-arithmethic for {@link FloatBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return
     */
    public static VoxelsArithmetic createFloat(
            Extent extent, IntFunction<FloatBuffer> bufferForSlice) {
        return new FloatImplementation(extent, bufferForSlice);
    }

    /**
     * Create voxels-arithmethic for {@link IntBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return
     */
    public static VoxelsArithmetic createInt(Extent extent, IntFunction<IntBuffer> bufferForSlice) {
        return new IntImplementation(extent, bufferForSlice);
    }
}
