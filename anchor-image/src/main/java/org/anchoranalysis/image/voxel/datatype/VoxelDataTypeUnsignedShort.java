/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

public class VoxelDataTypeUnsignedShort extends VoxelDataTypeUnsigned {

    public static final long MAX_VALUE = 65535;
    public static final int MAX_VALUE_INT = 65535;

    public static final VoxelDataTypeUnsignedShort INSTANCE = new VoxelDataTypeUnsignedShort();

    private VoxelDataTypeUnsignedShort() {
        super(16, "unsigned16", MAX_VALUE);
    }
}
