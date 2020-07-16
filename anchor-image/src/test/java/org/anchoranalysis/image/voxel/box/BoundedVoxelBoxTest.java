/* (C)2020 */
package org.anchoranalysis.image.voxel.box;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.junit.Test;

public class BoundedVoxelBoxTest {

    /**
     * Grows an object which is already partially outside the clip region
     *
     * @throws OperationFailedException
     */
    @Test(expected = OperationFailedException.class)
    public void testGrowObjectOutsideClipRegion() throws OperationFailedException {

        // A bounding box that overlaps with the extent
        Extent extent = new Extent(20, 20, 20);

        BoundedVoxelBox<ByteBuffer> box =
                new BoundedVoxelBox<ByteBuffer>(
                        new BoundingBox(new Point3i(10, 10, 10), new Extent(15, 15, 15)),
                        VoxelBoxFactory.getByte());

        Point3i grow = new Point3i(1, 1, 1);
        box.growBuffer(grow, grow, Optional.of(extent), VoxelBoxFactory.getByte());
    }
}
