/* (C)2020 */
package org.anchoranalysis.image.voxel.buffer.max;

import java.nio.Buffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;

/**
 * The buffer used when making a maximum-intensity projection
 *
 * @author Owen Feehan
 * @param <T> type of buffer used, both as input and result, of the maximum intesnity projection
 */
public abstract class MaxIntensityBuffer<T extends Buffer> {

    /** Target buffer, where the maximum-intensity pixels are stored */
    private VoxelBox<T> target;

    public MaxIntensityBuffer(Extent srcExtent, VoxelBoxFactoryTypeBound<T> factory) {
        target = factory.create(new Extent(srcExtent.getX(), srcExtent.getY(), 1));
    }

    public void projectSlice(T pixels) {

        T flatBuffer = target.getPixelsForPlane(0).buffer();

        while (pixels.hasRemaining()) {
            addBuffer(pixels, flatBuffer);
        }
    }

    /**
     * The target buffer
     *
     * @return the result of the maximum-intensity projection
     */
    public VoxelBox<T> getProjection() {
        return target;
    }

    protected abstract void addBuffer(T anySlice, T target);
}
