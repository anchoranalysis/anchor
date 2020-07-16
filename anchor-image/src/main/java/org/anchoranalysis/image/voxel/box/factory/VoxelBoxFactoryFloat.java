/* (C)2020 */
package org.anchoranalysis.image.voxel.box.factory;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxFloat;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromFloatBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;

final class VoxelBoxFactoryFloat implements VoxelBoxFactoryTypeBound<FloatBuffer> {

    private static final VoxelDataType DATA_TYPE = VoxelDataTypeFloat.INSTANCE;

    @Override
    public VoxelBox<FloatBuffer> create(PixelsForPlane<FloatBuffer> pixelsForPlane) {
        return new VoxelBoxFloat(pixelsForPlane);
    }

    @Override
    public VoxelBox<FloatBuffer> create(Extent e) {
        return new VoxelBoxFloat(PixelsFromFloatBufferArr.createInitialised(e));
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
