/* (C)2020 */
package org.anchoranalysis.image.voxel.neighborhood;

import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbor;

// 4 or 6 connectivity
final class SmallNeighborhood implements Neighborhood {

    @Override
    public void processAllPointsInNeighborhood(boolean do3D, ProcessVoxelNeighbor<?> process) {

        int numDims = do3D ? 3 : 2;

        process.notifyChangeZ(0);

        for (int d = 0; d < numDims; d++) {
            for (int j = -1; j <= 1; j += 2) {

                // If it's the z dimension we notify change in the Z value
                if (d == 2) {
                    if (process.notifyChangeZ(j)) {
                        processDim(process, j, d);
                    }
                } else {
                    processDim(process, j, d);
                }
            }
        }
    }

    private final void processDim(ProcessVoxelNeighbor<?> process, int j, int d) {
        switch (d) {
            case 0:
                process.processPoint(j, 0);
                break;
            case 1:
                process.processPoint(0, j);
                break;
            case 2:
                process.processPoint(0, 0);
                break;
            default:
                assert false;
        }
    }
}
