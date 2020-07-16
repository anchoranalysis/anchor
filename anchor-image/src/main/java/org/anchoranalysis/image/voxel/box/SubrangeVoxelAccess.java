/* (C)2020 */
package org.anchoranalysis.image.voxel.box;

import java.nio.Buffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/**
 * @author Owen Feehan
 * @param <T> buffer-type
 */
class SubrangeVoxelAccess<T extends Buffer> implements PixelsForPlane<T> {

    private int zRel;
    private Extent extent;
    private BoundedVoxelBox<T> src;

    public SubrangeVoxelAccess(int zRel, Extent extent, BoundedVoxelBox<T> src) {
        super();
        this.zRel = zRel;
        this.extent = extent;
        this.src = src;
    }

    @Override
    public void setPixelsForPlane(int z, VoxelBuffer<T> pixels) {
        src.getVoxelBox().setPixelsForPlane(z + zRel, pixels);
    }

    @Override
    public VoxelBuffer<T> getPixelsForPlane(int z) {
        return src.getVoxelBox().getPixelsForPlane(z + zRel);
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
