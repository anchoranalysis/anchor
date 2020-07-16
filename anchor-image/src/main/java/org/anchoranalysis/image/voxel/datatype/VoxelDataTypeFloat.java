/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

public class VoxelDataTypeFloat extends VoxelDataType {

    public static final long MAX_VALUE = (long) Float.MAX_VALUE;
    public static final long MIN_VALUE = (long) Float.MIN_VALUE;

    public static final VoxelDataTypeFloat INSTANCE = new VoxelDataTypeFloat();

    private VoxelDataTypeFloat() {
        super(32, "float", MAX_VALUE, MIN_VALUE);
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    @Override
    public boolean isUnsigned() {
        return false;
    }
}
