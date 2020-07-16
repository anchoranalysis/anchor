/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator.changed;

import java.nio.ByteBuffer;

/**
 * Processes a point which has been translated (changed) relative to another point - and includes
 * global coordinates and includes an object-mask buffer
 *
 * @param <T> result-type that can be collected after processing
 */
public interface ProcessChangedPointAbsoluteMasked<T> {

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

    /** Notifies the processor that there has been a change in z-coordinate */
    default void notifyChangeZ(int zChange, int z, ByteBuffer objectMaskBuffer) {}

    /** Processes a particular point */
    boolean processPoint(int xChange, int yChange, int x1, int y1, int objectMaskOffset);

    /** Collects the result of the operation after processing neighbor pixels */
    public abstract T collectResult();
}
