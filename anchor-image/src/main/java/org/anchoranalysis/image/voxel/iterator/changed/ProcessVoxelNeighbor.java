/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator.changed;

import org.anchoranalysis.core.geometry.Point3i;

/**
 * Processes a point that is an neighbor of another.
 *
 * <p>It assumes there is an associated sliding buffer containing voxel-values.
 *
 * @author Owen Feehan
 * @param <T> result-type that can be collected after processing
 */
public interface ProcessVoxelNeighbor<T> {

    /**
     * Specify the source-point (of which all the processed points are neighbors)
     *
     * <p>This must be called before any calls to {@link processPoint}.
     *
     * @param pointSource the source point in global coordinates
     * @param sourceVal the value of the source pixel (in the associated sliding buffer)
     * @param sourceOffsetXY the offset of the source pixel in XY (in the associated sliding buffer)
     */
    void initSource(Point3i pointSource, int sourceVal, int sourceOffsetXY);

    /**
     * Notifies the processor that there has been a change in z-coordinate
     *
     * @param zChange the change in the Z-dimension to reach this neighbor relative to the source
     *     coordinate
     * @return true if processing should continue on this slice, or false if processing should stop
     *     for this slice
     */
    default boolean notifyChangeZ(int zChange) {
        return true;
    }

    /**
     * Processes a particular point
     *
     * @param zChange the change in X-dimension to reach this neighbor relative to the source
     *     coordinate
     * @param yChange the change in Y-dimension to reach this neighbor relative to the source
     *     coordinate
     */
    void processPoint(int xChange, int yChange);

    /** Collects the result of the operation after processing neighbor pixels */
    public abstract T collectResult();
}
