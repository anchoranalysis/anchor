/* (C)2020 */
package org.anchoranalysis.image.voxel.box.factory;

import java.nio.IntBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxInt;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromIntBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;

final class VoxelBoxFactoryInt implements VoxelBoxFactoryTypeBound<IntBuffer> {

    private static final VoxelDataType DATA_TYPE = VoxelDataTypeUnsignedInt.INSTANCE;

    @Override
    public VoxelBox<IntBuffer> create(PixelsForPlane<IntBuffer> pixelsForPlane) {
        return new VoxelBoxInt(pixelsForPlane);
    }

    @Override
    public VoxelBox<IntBuffer> create(Extent e) {
        return new VoxelBoxInt(PixelsFromIntBufferArr.createInitialised(e));
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
