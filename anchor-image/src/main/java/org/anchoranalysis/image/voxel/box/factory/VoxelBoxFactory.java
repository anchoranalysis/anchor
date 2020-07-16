/* (C)2020 */
package org.anchoranalysis.image.voxel.box.factory;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.factory.VoxelDataTypeFactoryMultiplexer;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class VoxelBoxFactory
        extends VoxelDataTypeFactoryMultiplexer<VoxelBoxFactoryTypeBound<? extends Buffer>> {

    // Singleton
    private static VoxelBoxFactory instance;

    private static final VoxelBoxFactoryTypeBound<ByteBuffer> FACTORY_BYTE =
            new VoxelBoxFactoryByte();
    private static final VoxelBoxFactoryTypeBound<ShortBuffer> FACTORY_SHORT =
            new VoxelBoxFactoryShort();
    private static final VoxelBoxFactoryTypeBound<IntBuffer> FACTORY_INT = new VoxelBoxFactoryInt();
    private static final VoxelBoxFactoryTypeBound<FloatBuffer> FACTORY_FLOAT =
            new VoxelBoxFactoryFloat();

    private VoxelBoxFactory() {
        super(FACTORY_BYTE, FACTORY_SHORT, FACTORY_INT, FACTORY_FLOAT);
    }

    /** Singleton */
    public static VoxelBoxFactory instance() {
        if (instance == null) {
            instance = new VoxelBoxFactory();
        }
        return instance;
    }

    public <T extends Buffer> VoxelBoxWrapper create(
            PixelsForPlane<T> pixelsForPlane, VoxelDataType dataType) {
        @SuppressWarnings("unchecked")
        VoxelBoxFactoryTypeBound<T> factory = (VoxelBoxFactoryTypeBound<T>) get(dataType);
        VoxelBox<T> buffer = factory.create(pixelsForPlane);
        return new VoxelBoxWrapper(buffer);
    }

    public VoxelBoxWrapper create(Extent e, VoxelDataType dataType) {
        VoxelBoxFactoryTypeBound<?> factory = get(dataType);
        VoxelBox<? extends Buffer> buffer = factory.create(e);
        return new VoxelBoxWrapper(buffer);
    }

    public static VoxelBoxFactoryTypeBound<ByteBuffer> getByte() {
        return FACTORY_BYTE;
    }

    public static VoxelBoxFactoryTypeBound<ShortBuffer> getShort() {
        return FACTORY_SHORT;
    }

    public static VoxelBoxFactoryTypeBound<IntBuffer> getInt() {
        return FACTORY_INT;
    }

    public static VoxelBoxFactoryTypeBound<FloatBuffer> getFloat() {
        return FACTORY_FLOAT;
    }
}
