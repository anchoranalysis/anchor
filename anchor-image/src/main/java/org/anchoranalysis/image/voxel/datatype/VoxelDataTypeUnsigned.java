/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

public abstract class VoxelDataTypeUnsigned extends VoxelDataType {

    protected VoxelDataTypeUnsigned(int numBits, String typeIdentifier, long maxValue) {
        super(numBits, typeIdentifier, maxValue, 0);
    }

    @Override
    public final boolean isInteger() {
        return true;
    }

    @Override
    public final boolean isUnsigned() {
        return true;
    }
}
