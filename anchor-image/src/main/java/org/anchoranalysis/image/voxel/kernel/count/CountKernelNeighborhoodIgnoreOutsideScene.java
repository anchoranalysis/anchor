/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.count;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;

/**
 * For every voxel on the outline, count ALL neighbors that are adjacent, but ignoring any outside
 * the scene.
 *
 * <p>Neighboring voxels can be counted more than once.
 *
 * @author Owen Feehan
 */
public class CountKernelNeighborhoodIgnoreOutsideScene extends CountKernelNeighborhoodBase {

    private Extent extentScene;
    private ReadableTuple3i addPoint;

    public CountKernelNeighborhoodIgnoreOutsideScene(
            boolean useZ,
            BinaryValuesByte bv,
            boolean multipleMatchesPerVoxel,
            Extent extentScene, // The entire extent of the scene
            ReadableTuple3i
                    addPoint // Added to a point before determining if it is inside or outside the
            // scene.
            ) {
        super(useZ, bv, multipleMatchesPerVoxel);
        this.extentScene = extentScene;
        this.addPoint = addPoint;
    }

    @Override
    protected boolean isNeighborVoxelAccepted(
            Point3i point, int xShift, int yShift, int zShift, Extent extent) {
        return extentScene.contains(
                point.getX() + xShift + addPoint.getX(),
                point.getY() + yShift + addPoint.getY(),
                point.getZ() + zShift + addPoint.getZ());
    }
}
