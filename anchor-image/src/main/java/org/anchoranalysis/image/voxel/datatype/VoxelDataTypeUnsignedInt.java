/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

public class VoxelDataTypeUnsignedInt extends VoxelDataTypeUnsigned {

    public static final long MAX_VALUE = 4294967295L;

    public static final VoxelDataTypeUnsignedInt INSTANCE = new VoxelDataTypeUnsignedInt();

    private VoxelDataTypeUnsignedInt() {
        super(32, "unsigned32", MAX_VALUE);
    }
}
