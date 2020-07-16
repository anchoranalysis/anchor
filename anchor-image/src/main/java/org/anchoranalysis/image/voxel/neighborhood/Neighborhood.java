/* (C)2020 */
package org.anchoranalysis.image.voxel.neighborhood;

import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbor;

public interface Neighborhood {

    void processAllPointsInNeighborhood(boolean do3D, ProcessVoxelNeighbor<?> processNeighbor);
}
