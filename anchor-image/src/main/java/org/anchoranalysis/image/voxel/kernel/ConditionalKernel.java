/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.box.VoxelBox;

// Erosion with a 3x3 or 3x3x3 kernel
public class ConditionalKernel extends BinaryKernel {

    private BinaryKernel kernel;
    private int minValue;
    private VoxelBox<ByteBuffer> vbIntensity;

    // Constructor
    public ConditionalKernel(BinaryKernel kernel, int minValue, VoxelBox<ByteBuffer> vbIntensity) {
        super(kernel.getSize());
        this.kernel = kernel;
        this.minValue = minValue;
        this.vbIntensity = vbIntensity;
    }

    @Override
    public boolean accptPos(int ind, Point3i point) {

        byte valByte =
                vbIntensity
                        .getPixelsForPlane(point.getZ())
                        .buffer()
                        .get(vbIntensity.extent().offsetSlice(point));
        int val = ByteConverter.unsignedByteToInt(valByte);

        if (val < minValue) {
            return false;
        }

        return kernel.accptPos(ind, point);
    }

    @Override
    public void init(VoxelBox<ByteBuffer> in) {
        kernel.init(in);
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        kernel.notifyZChange(inSlices, z);
    }
}
