/* (C)2020 */
package org.anchoranalysis.image.binary.voxel;

import java.nio.IntBuffer;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class BinaryVoxelBoxInt extends BinaryVoxelBox<IntBuffer> {

    public BinaryVoxelBoxInt(VoxelBox<IntBuffer> voxelBox, BinaryValues bv) {
        super(voxelBox, bv);
    }

    @Override
    public boolean isOn(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        return getVoxelBox().getPixelsForPlane(z).buffer().get(offset)
                != getBinaryValues().getOffInt();
    }

    @Override
    public boolean isOff(int x, int y, int z) {
        return !isOn(x, y, z);
    }

    @Override
    public void setOn(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        getVoxelBox().getPixelsForPlane(z).buffer().put(offset, getBinaryValues().getOnInt());
    }

    @Override
    public void setOff(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        getVoxelBox().getPixelsForPlane(z).buffer().put(offset, getBinaryValues().getOffInt());
    }

    @Override
    public BinaryVoxelBox<IntBuffer> duplicate() {
        return new BinaryVoxelBoxInt(getVoxelBox().duplicate(), getBinaryValues());
    }

    public BinaryVoxelBox<IntBuffer> extractSlice(int z) {
        return new BinaryVoxelBoxInt(getVoxelBox().extractSlice(z), getBinaryValues());
    }
}
