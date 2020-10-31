package org.anchoranalysis.image.voxel.buffer.max;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.Extent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates buffers for performing a <a href="https://en.wikipedia.org/wiki/Maximum_intensity_projection">Maximum Intensity Projection</a>.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class MaxIntensityProjection {

    public static ProjectableBuffer<UnsignedByteBuffer> createUnsignedByte(Extent extent) {
        return new UnsignedByteImplementation(extent);
    }
    
    public static ProjectableBuffer<UnsignedShortBuffer> createUnsignedShort(Extent extent) {
        return new UnsignedShortImplementation(extent);
    }
    
    public static ProjectableBuffer<UnsignedIntBuffer> createUnsignedInt(Extent extent) {
        return new UnsignedIntImplementation(extent);
    }
    
    public static ProjectableBuffer<FloatBuffer> createFloat(Extent extent) {
        return new FloatImplementation(extent);
    }
}
