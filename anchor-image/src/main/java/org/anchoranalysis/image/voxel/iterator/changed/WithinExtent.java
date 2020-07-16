/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator.changed;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;

/**
 * Only processes points within a certain extent
 *
 * <p>Any points outside this extent are rejected.
 *
 * @param <T> result-type that can be collected after processing
 * @author Owen Feehan
 */
final class WithinExtent<T> implements ProcessVoxelNeighbor<T> {

    private final Extent extent;
    private final ProcessVoxelNeighborAbsolute<T> delegate;

    private Point3i point;

    public WithinExtent(Extent extent, ProcessVoxelNeighborAbsolute<T> processAbsolutePoint) {
        this.extent = extent;
        this.delegate = processAbsolutePoint;
    }

    @Override
    public void initSource(Point3i point, int sourceVal, int sourceOffsetXY) {
        this.point = point;
        this.delegate.initSource(sourceVal, sourceOffsetXY);
    }

    @Override
    public void processPoint(int xChange, int yChange) {

        int x1 = point.getX() + xChange;
        int y1 = point.getY() + yChange;

        if (x1 < 0 || x1 >= extent.getX() || y1 < 0 || y1 >= extent.getY()) {
            return;
        }

        delegate.processPoint(xChange, yChange, x1, y1);
    }

    @Override
    public boolean notifyChangeZ(int zChange) {
        int z1 = point.getZ() + zChange;

        if (!extent.containsZ(z1)) {
            return false;
        }

        delegate.notifyChangeZ(zChange, z1);
        return true;
    }

    @Override
    public T collectResult() {
        return delegate.collectResult();
    }
}
