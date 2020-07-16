/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.count;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;

/**
 * For every voxel on the outline, count ALL neighbors that are adjacent, including those lying
 * outside the scene.
 *
 * <p>Neighbouing voxels can be counted more than once.
 *
 * @author Owen Feehan
 */
public class CountKernelNeighborhood extends CountKernelNeighborhoodBase {

    public CountKernelNeighborhood(
            boolean useZ, BinaryValuesByte bv, boolean multipleMatchesPerVoxel) {
        super(useZ, bv, multipleMatchesPerVoxel);
    }

    @Override
    protected boolean isNeighborVoxelAccepted(
            Point3i point, int xShift, int yShift, int zShift, Extent extent) {
        return true;
    }
}
