package org.anchoranalysis.image.voxel.kernel;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ObjectOnBinaryHelper {
    
    /**
     * Creates {@link Voxels} showing an object on top of an otherwise empty background.
     * 
     * @param object the object to show
     * @param extent the size of the {@link Voxels} to create.
     * @param objectOn if true, the objects use <i>on</i> and the background <i>off</i>. if false, the opposite combination.
     * @return a newly created {@link BinaryVoxels} using default values for <i>off</i> and </i>on</i>.
     */
    public static BinaryVoxels<UnsignedByteBuffer> createVoxelsWithObject( ObjectMask object, Extent extent, boolean objectOn ) {
        if (objectOn) {
            BinaryVoxels<UnsignedByteBuffer> voxels = BinaryVoxelsFactory.createEmptyOff(extent);
            voxels.assignOn().toObject(object);
            return voxels;
        } else {
            BinaryVoxels<UnsignedByteBuffer> voxels = BinaryVoxelsFactory.createEmptyOn(extent);
            voxels.assignOff().toObject(object);
            return voxels;
        }
    }
}
