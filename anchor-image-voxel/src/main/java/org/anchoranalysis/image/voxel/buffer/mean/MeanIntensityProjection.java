package org.anchoranalysis.image.voxel.buffer.mean;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.spatial.Extent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates buffers for performing a mean-intensity-projection.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class MeanIntensityProjection {

    public static ProjectableBuffer<UnsignedByteBuffer> createUnsignedByte(Extent extent) {
        return new MeanIntensityBuffer<>(VoxelsFactory.getUnsignedByte(), extent);
    }
    
    public static ProjectableBuffer<UnsignedShortBuffer> createUnsignedShort(Extent extent) {
        return new MeanIntensityBuffer<>(VoxelsFactory.getUnsignedShort(), extent);
    }
    
    public static ProjectableBuffer<UnsignedIntBuffer> createUnsignedInt(Extent extent) {
        return new MeanIntensityBuffer<>(VoxelsFactory.getUnsignedInt(), extent);
    }
    
    public static ProjectableBuffer<FloatBuffer> createFloat(Extent extent) {
        return new MeanIntensityBuffer<>(VoxelsFactory.getFloat(), extent);
    }
}
