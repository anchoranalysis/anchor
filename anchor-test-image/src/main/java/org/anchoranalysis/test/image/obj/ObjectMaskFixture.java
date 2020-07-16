/* (C)2020 */
package org.anchoranalysis.test.image.obj;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

@AllArgsConstructor
public class ObjectMaskFixture {

    private final ImageDimensions dimensions;

    public ObjectMask create1() {
        Extent extent = new Extent(20, 34, 11);
        CutOffCorners pattern = new CutOffCorners(3, 2, extent);
        return createAt(new Point3i(10, 15, 3), extent, pattern);
    }

    public ObjectMask create2() {
        Extent extent = new Extent(19, 14, 5);
        CutOffCorners pattern = new CutOffCorners(5, 1, extent);
        return createAt(new Point3i(3, 1, 7), extent, pattern);
    }

    public ObjectMask create3() {
        Extent extent = new Extent(19, 14, 13);
        CutOffCorners pattern = new CutOffCorners(1, 5, extent);
        return createAt(new Point3i(17, 15, 2), extent, pattern);
    }

    private ObjectMask createAt(Point3i cornerMin, Extent extent, VoxelPattern pattern) {
        BoundingBox bbox = new BoundingBox(cornerMin, extent);

        assertTrue(dimensions.contains(bbox));

        VoxelBox<ByteBuffer> vb = VoxelBoxFactory.getByte().create(extent);
        BinaryValues bv = BinaryValues.getDefault();
        BinaryValuesByte bvb = bv.createByte();

        boolean atLeastOneHigh = false;

        for (int z = 0; z < extent.getZ(); z++) {
            VoxelBuffer<ByteBuffer> slice = vb.getPixelsForPlane(z);

            for (int y = 0; y < extent.getY(); y++) {
                for (int x = 0; x < extent.getX(); x++) {
                    byte toPut;
                    if (pattern.isPixelOn(x, y, z)) {
                        toPut = bvb.getOnByte();
                        atLeastOneHigh = true;
                    } else {
                        toPut = bvb.getOffByte();
                    }
                    slice.putByte(extent.offset(x, y), toPut);
                }
            }
        }

        assertTrue(atLeastOneHigh);

        return new ObjectMask(bbox, new BinaryVoxelBoxByte(vb, bv));
    }
}
