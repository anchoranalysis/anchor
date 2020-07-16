/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.count;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

/**
 * The number of touching-faces of a voxel with a neighbor, so long as the neighbor is part of a
 * mask
 *
 * <p>i.e. the sum of all faces of a voxel that touch the face of a voxel belonging to a neighboring
 * pixel
 *
 * @author Owen Feehan
 */
public class CountKernelNeighborhoodMask extends CountKernelNeighborhoodBase {

    private BinaryVoxelBox<ByteBuffer> vbRequireHigh;
    private BinaryValuesByte bvRequireHigh;
    private ObjectMask objectRequireHigh;

    private LocalSlices localSlicesRequireHigh;

    public CountKernelNeighborhoodMask(
            boolean useZ,
            BinaryValuesByte bv,
            ObjectMask objectRequireHigh,
            boolean multipleMatchesPerVoxel) {
        super(useZ, bv, multipleMatchesPerVoxel);
        this.objectRequireHigh = objectRequireHigh;
        this.vbRequireHigh = objectRequireHigh.binaryVoxelBox();
        this.bvRequireHigh = vbRequireHigh.getBinaryValues().createByte();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        super.notifyZChange(inSlices, z);
        localSlicesRequireHigh =
                new LocalSlices(
                        z + objectRequireHigh.getBoundingBox().cornerMin().getZ(),
                        3,
                        vbRequireHigh.getVoxelBox());
    }

    @Override
    protected boolean isNeighborVoxelAccepted(
            Point3i point, int xShift, int yShift, int zShift, Extent extent) {

        ByteBuffer inArr = localSlicesRequireHigh.getLocal(zShift);

        if (inArr == null) {
            return false;
        }

        int x1 = point.getX() + objectRequireHigh.getBoundingBox().cornerMin().getX() + xShift;

        if (!vbRequireHigh.extent().containsX(x1)) {
            return false;
        }

        int y1 = point.getY() + objectRequireHigh.getBoundingBox().cornerMin().getY() + yShift;

        if (!vbRequireHigh.extent().containsY(y1)) {
            return false;
        }

        int indexGlobal = vbRequireHigh.extent().offset(x1, y1);
        return bvRequireHigh.isOn(inArr.get(indexGlobal));
    }
}
