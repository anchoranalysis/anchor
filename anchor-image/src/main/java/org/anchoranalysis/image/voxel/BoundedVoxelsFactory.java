package org.anchoranalysis.image.voxel;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class BoundedVoxelsFactory {

    public static BoundedVoxels<ByteBuffer> createByte(BoundingBox box) {
        return new BoundedVoxels<>(
                box,
                VoxelsFactory.getByte().createInitialized(box.extent()));
    }
    
    public static BoundedVoxels<IntBuffer> createInt(BoundingBox box) {
        return new BoundedVoxels<>(
                box,
                VoxelsFactory.getInt().createInitialized(box.extent()));
    }
}
