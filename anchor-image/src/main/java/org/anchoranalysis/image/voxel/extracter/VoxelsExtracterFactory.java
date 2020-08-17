package org.anchoranalysis.image.voxel.extracter;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.voxel.Voxels;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class VoxelsExtracterFactory {

    /**
     * Create voxels-extracter for {@link ByteBuffer}
     * 
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<ByteBuffer> createByte(Voxels<ByteBuffer> voxels) {
        return new ByteImplementation(voxels);
    }
    
    /**
     * Create voxels-extracter for {@link ShortBuffer}
     * 
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<ShortBuffer> createShort(Voxels<ShortBuffer> voxels) {
        return new ShortImplementation(voxels);
    }
    
    /**
     * Create voxels-extracter for {@link FloatBuffer}
     * 
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<FloatBuffer> createFloat(Voxels<FloatBuffer> voxels) {
        return new FloatImplementation(voxels);
    }
    
    /**
     * Create voxels-extracter for {@link IntBuffer}
     * 
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<IntBuffer> createInt(Voxels<IntBuffer> voxels) {
        return new IntImplementation(voxels);
    }
    
    /**
     * Projects a {@link VoxelsExtracter} to a corner in a larger global space
     * <p>
     * Coordinates are translated appropriately for any calls from the larger global space
     * to the space on which {@code delegate} is defined.
     * 
     * @param <T> buffer-type
     * @param corner the corner at which the voxels referred to by {@code delegate} are considered to exist
     * @param delegate the delegate
     * @return an extracter that performs translation from global-coordinates to the coordinate system expected by the delegate
     */
    public static <T extends Buffer> VoxelsExtracter<T> atCorner(ReadableTuple3i corner, VoxelsExtracter<T> delegate) {
        return new AtCorner<>(corner, delegate);
    }
}
