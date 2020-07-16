/* (C)2020 */
package org.anchoranalysis.image.interpolator;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public interface Interpolator {

    /**
     * Interpolates from src to dest. Both boxes must be 2-dimensional. Returns the destination
     * buffer (either as passed, or a new one that was created)
     *
     * @param src
     * @param dest
     */
    VoxelBuffer<ByteBuffer> interpolateByte(
            VoxelBuffer<ByteBuffer> src, VoxelBuffer<ByteBuffer> dest, Extent eSrc, Extent eDest);

    VoxelBuffer<ShortBuffer> interpolateShort(
            VoxelBuffer<ShortBuffer> src, VoxelBuffer<ShortBuffer> dest, Extent eSrc, Extent eDest);

    /**
     * Returns TRUE if it's possible for values to be created after interpolation that aren't found
     * in the input-image. Returns the destination buffer (either as passed, or a new one that was
     * created)
     *
     * @return
     */
    boolean isNewValuesPossible();
}
