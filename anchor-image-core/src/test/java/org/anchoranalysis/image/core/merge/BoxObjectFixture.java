package org.anchoranalysis.image.core.merge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * Creates {@link ObjectMask} whose <i>on</i> voxels look like two- or three-dimensional boxes.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BoxObjectFixture {

    /**
     * Creates an {@link ObjectMask} that is a box in 2D.
     *
     * @param coordinate the minimum corner in all three dimensions.
     * @param extent the size in all three dimensions.
     * @return a newly created {@link ObjectMask}.
     */
    public static ObjectMask create2D(int coordinate, int extent) {
        BoundingBox box = BoundingBoxFactory.at(coordinate, coordinate, extent, extent);
        return createFromBox(box);
    }

    /**
     * Creates an {@link ObjectMask} that is a box in 3D.
     *
     * @param coordinate the minimum corner in all three dimensions.
     * @param extent the size in all three dimensions.
     * @return a newly created {@link ObjectMask}.
     */
    public static ObjectMask create3D(int coordinate, int extent) {
        return createFromBox(BoundingBoxFactory.uniform3D(coordinate, extent));
    }

    private static ObjectMask createFromBox(BoundingBox box) {
        ObjectMask object = new ObjectMask(box);
        object = object.invert();
        return object;
    }
}
