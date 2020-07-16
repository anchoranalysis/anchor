/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

public class VoxelDataTypeUnsignedByte extends VoxelDataTypeUnsigned {

    public static final long MAX_VALUE = 255;
    public static final int MAX_VALUE_INT = 255;

    public static final VoxelDataTypeUnsignedByte INSTANCE = new VoxelDataTypeUnsignedByte();

    private VoxelDataTypeUnsignedByte() {
        super(8, "unsigned8", MAX_VALUE);
    }
}
