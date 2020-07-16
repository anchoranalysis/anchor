/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;

/**
 * Processes a 3D point like {@link ProcessVoxelOffset} but also retrieves a {@link ByteBuffer} for
 * the current z-slice.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface ProcessVoxelSliceBuffer<T extends Buffer> {

    /** Notifies the processor that there has been a change in z-coordinate */
    default void notifyChangeZ(int z) {}

    /**
     * Processes a voxel location in a buffer
     *
     * @param point a point with global (absolute coordinates)
     * @param buffer a buffer for the current slice for which {@link offsetSlice} refers to a
     *     particular location
     * @param offset an offset value for the current slice (i.e. indexing XY only, but not Z)
     */
    void process(Point3i point, T buffer, int offset);
}
