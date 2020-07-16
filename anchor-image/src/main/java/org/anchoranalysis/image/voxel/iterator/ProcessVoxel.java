/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.core.geometry.Point3i;

/**
 * Processes a 3D point
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface ProcessVoxel {

    /** Notifies the processor that there has been a change in y-coordinate */
    default void notifyChangeY(int y) {}

    /** Notifies the processor that there has been a change in z-coordinate */
    default void notifyChangeZ(int z) {}

    void process(Point3i point);
}
