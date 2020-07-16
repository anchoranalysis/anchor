/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

public abstract class VoxelDataTypeSigned extends VoxelDataType {

    protected VoxelDataTypeSigned(
            int numBits, String typeIdentifier, long maxValue, long minValue) {
        super(numBits, typeIdentifier, maxValue, minValue);
    }

    @Override
    public final boolean isInteger() {
        return true;
    }

    @Override
    public final boolean isUnsigned() {
        return false;
    }
}
