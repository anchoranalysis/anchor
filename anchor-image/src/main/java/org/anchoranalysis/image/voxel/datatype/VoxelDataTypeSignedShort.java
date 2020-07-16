/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

public class VoxelDataTypeSignedShort extends VoxelDataTypeSigned {

    public static final long MIN_VALUE = -32768;
    public static final long MAX_VALUE = 32767;

    public static final VoxelDataTypeSignedShort instance = new VoxelDataTypeSignedShort();

    private VoxelDataTypeSignedShort() {
        super(16, "signed16", MAX_VALUE, MIN_VALUE);
    }
}
