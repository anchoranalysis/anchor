/* (C)2020 */
package org.anchoranalysis.image.voxel.neighborhood;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbor;

/**
 * Provides either 8-connectivity or 26-connectivity as an neighborhood.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
final class BigNeighborhood implements Neighborhood {

    private final boolean includeCenterPoint;

    public BigNeighborhood() {
        this.includeCenterPoint = false;
    }

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public void processAllPointsInNeighborhood(boolean do3D, ProcessVoxelNeighbor<?> process) {

        if (do3D) {

            for (int z = -1; z <= 1; z++) {

                if (!process.notifyChangeZ(z)) {
                    continue;
                }

                for (int y = -1; y <= 1; y++) {
                    for (int x = -1; x <= 1; x++) {
                        if (includeCenterPoint || x != 0 || y != 0 || z != 0) {
                            process.processPoint(x, y);
                        }
                    }
                }
            }

        } else {

            if (!process.notifyChangeZ(0)) {
                return;
            }

            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    if (includeCenterPoint || x != 0 || y != 0) {
                        process.processPoint(x, y);
                    }
                }
            }
        }
    }
}
