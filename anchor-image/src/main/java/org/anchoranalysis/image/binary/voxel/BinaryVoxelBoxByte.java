/* (C)2020 */
package org.anchoranalysis.image.binary.voxel;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class BinaryVoxelBoxByte extends BinaryVoxelBox<ByteBuffer> {

    private BinaryValuesByte bvb;

    public BinaryVoxelBoxByte(VoxelBox<ByteBuffer> voxelBox, BinaryValues bv) {
        super(voxelBox, bv);
        this.bvb = getBinaryValues().createByte();
    }

    @Override
    public boolean isOn(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        return getVoxelBox().getPixelsForPlane(z).buffer().get(offset) != bvb.getOffByte();
    }

    @Override
    public boolean isOff(int x, int y, int z) {
        return !isOn(x, y, z);
    }

    @Override
    public void setOn(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        getVoxelBox().getPixelsForPlane(z).buffer().put(offset, bvb.getOnByte());
    }

    @Override
    public void setOff(int x, int y, int z) {
        int offset = getVoxelBox().extent().offset(x, y);
        getVoxelBox().getPixelsForPlane(z).buffer().put(offset, bvb.getOffByte());
    }

    public BinaryValuesByte getBinaryValuesByte() {
        return bvb;
    }

    @Override
    public BinaryVoxelBoxByte duplicate() {
        return new BinaryVoxelBoxByte(getVoxelBox().duplicate(), getBinaryValues());
    }

    public BinaryVoxelBox<ByteBuffer> extractSlice(int z) throws CreateException {
        return new BinaryVoxelBoxByte(getVoxelBox().extractSlice(z), getBinaryValues());
    }
}
