/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class AndKernel extends BinaryKernel {

    private BinaryKernel kernel1;
    private BinaryKernel kernel2;

    public AndKernel(BinaryKernel kernel1, BinaryKernel kernel2) {
        super(kernel1.getSize());
        this.kernel1 = kernel1;
        this.kernel2 = kernel2;
    }

    @Override
    public void init(VoxelBox<ByteBuffer> in) {
        kernel1.init(in);
        kernel2.init(in);
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        kernel1.notifyZChange(inSlices, z);
        kernel2.notifyZChange(inSlices, z);
    }

    @Override
    public boolean accptPos(int ind, Point3i point) {
        return kernel1.accptPos(ind, point) && kernel2.accptPos(ind, point);
    }
}
