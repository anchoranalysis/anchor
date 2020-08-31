package org.anchoranalysis.io.imagej.convert;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import ij.process.ImageProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Converts a {@link ImageProcessor} to a voxel-buffer of particular data-type.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ConvertToVoxelBuffer {

    /**
     * Convert a {@link ImageProcessor} to {@code VoxelBuffer<ByteBuffer>}
     * 
     * @param processor the processor to convert
     * @return a voxel-buffer that reuses the memory of the processor (no duplication)
     */
    public static VoxelBuffer<ByteBuffer> asByte(ImageProcessor processor) {
        byte[] arr = (byte[]) processor.getPixels();
        return VoxelBufferByte.wrap(arr);
    }

    /**
     * Convert a {@link ImageProcessor} to {@code VoxelBuffer<ShortBuffer>}
     * 
     * @param processor the processor to convert
     * @return a voxel-buffer that reuses the memory of the processor (no duplication)
     */
    public static VoxelBuffer<ShortBuffer> asShort(ImageProcessor processor) {
        short[] arr = (short[]) processor.getPixels();
        return VoxelBufferShort.wrap(arr);
    }
}
