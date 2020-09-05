package org.anchoranalysis.image.voxel.iterator.neighbor;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.neighborhood.Neighborhood;

/**
 * Utilities for iterating over the neighboring voxels to a given point.
 *
 * @author Owen Feehan
 */
public class IterateVoxelsNeighbors {

    /**
     * Iterate over each point in the neighborhood of an existing point.
     * 
     * <p>It also sets the source in {@code process}.
     *
     * @param sourcePoint the point to iterate over its neighborhood
     * @param neighborhood a definition of what constitutes the neighborhood
     * @param do3D whether to iterate in 2D or 3D
     * @param process is called for each voxel in the neighborhood of the source-point.
     * @return the result after processing each point in the neighborhood
     */
    public static <T> T callEachPointInNeighborhood(
            Point3i sourcePoint,
            Neighborhood neighborhood,
            boolean do3D,
            ProcessVoxelNeighbor<T> process,
            int sourceVal,
            int sourceOffsetXY) {
        process.initSource(sourcePoint, sourceVal, sourceOffsetXY);
        neighborhood.processAllPointsInNeighborhood(do3D, process);
        return process.collectResult();
    }
}
