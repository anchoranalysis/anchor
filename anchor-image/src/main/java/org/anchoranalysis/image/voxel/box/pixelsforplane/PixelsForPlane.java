/* (C)2020 */
package org.anchoranalysis.image.voxel.box.pixelsforplane;

import java.nio.Buffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/**
 * @author Owen Feehan
 * @param <T> buffer-type
 */
public interface PixelsForPlane<T extends Buffer> {

    public VoxelBuffer<T> getPixelsForPlane(int z);

    public void setPixelsForPlane(int z, VoxelBuffer<T> pixels);

    public Extent extent();
}
