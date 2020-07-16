/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

import org.anchoranalysis.core.error.CreateException;

public class CombineTypes {

    private CombineTypes() {}

    public static VoxelDataType combineTypes(VoxelDataType type1, VoxelDataType type2)
            throws CreateException {
        if (type1.equals(type2)) {
            return type1;
        } else if (type1.equals(VoxelDataTypeUnsignedByte.INSTANCE)
                && type2.equals(VoxelDataTypeUnsignedShort.INSTANCE)) {
            return VoxelDataTypeUnsignedShort.INSTANCE;
        } else if (type2.equals(VoxelDataTypeUnsignedByte.INSTANCE)
                && type1.equals(VoxelDataTypeUnsignedShort.INSTANCE)) {
            return VoxelDataTypeUnsignedShort.INSTANCE;
        } else {
            throw new CreateException("Only combinations of byte and short are supported");
        }
    }
}
