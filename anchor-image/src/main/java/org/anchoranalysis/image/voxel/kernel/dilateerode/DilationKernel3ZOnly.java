/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.dilateerode;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.box.VoxelBox;

// Erosion with a 3x3 or 3x3x3 kernel
public final class DilationKernel3ZOnly extends BinaryKernelMorph3 {

    // Constructor
    public DilationKernel3ZOnly(BinaryValuesByte bv, boolean outsideAtThreshold) {
        super(bv, outsideAtThreshold);
    }

    @Override
    public void init(VoxelBox<ByteBuffer> in) {
        // NOTHING TO DO
    }

    @Override
    public boolean accptPos(int ind, Point3i point) {

        ByteBuffer inArrZ = inSlices.getLocal(0);
        ByteBuffer inArrZLess1 = inSlices.getLocal(-1);
        ByteBuffer inArrZPlus1 = inSlices.getLocal(+1);

        if (bv.isOn(inArrZ.get(ind))) {
            return true;
        }

        if (inArrZLess1 != null) {
            if (bv.isOn(inArrZLess1.get(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }

        if (inArrZPlus1 != null) {
            if (bv.isOn(inArrZPlus1.get(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }

        return false;
    }
}
