/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator.changed;

/**
 * Processes a point that is a neighbor of another - includes global (absolute) coordinates for this
 * point.
 *
 * @param <T> result-type that can be collected after processing
 */
public interface ProcessVoxelNeighborAbsolute<T> {

    /**
     * The value and offset for the source point (around which we process neighbors)
     *
     * <p>This function should always be called before {@link processPoint}
     *
     * <p>It can be called repeatedly for different points (resetting state each time).
     *
     * @param sourceVal the value of the source pixel
     * @param sourceOffsetXY the offset of the source pixel in XY
     */
    void initSource(int sourceVal, int sourceOffsetXY);

    /**
     * Notifies the processor that there has been a change in z-coordinate
     *
     * @param zChange the change in the Z-dimension to reach this neighbor relative to the source
     *     coordinate
     */
    default void notifyChangeZ(int zChange, int z) {}

    /**
     * Processes a particular point
     *
     * @param zChange the change in X-dimension to reach this neighbor relative to the source
     *     coordinate
     * @param yChange the change in Y-dimension to reach this neighbor relative to the source
     *     coordinate
     * @param x the cordinates for this point (the neighboring point) in global (absolute) terms
     *     i.e. NOT relative to a bounding-box
     * @param y the cordinates for this point (the neighboring point) in global (absolute) terms
     *     i.e. NOT relative to a bounding-box
     */
    boolean processPoint(int xChange, int yChange, int x, int y);

    /** Collects the result of the operation after processing neighbor pixels */
    public abstract T collectResult();
}
