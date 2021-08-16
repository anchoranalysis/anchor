package org.anchoranalysis.image.core.merge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ObjectMaskMerger}.
 *
 * @author Owen Feehan
 */
class ObjectMaskMergerTest {

    private static final ObjectMask OBJECT1 = object(10, 20);
    private static final ObjectMask OBJECT2 = object(15, 25);

    @Test
    void testMerge() {
        ObjectMask merged = ObjectMaskMerger.merge(OBJECT1, OBJECT2);
        assertEquals(20250, merged.numberVoxelsOn());
    }

    private static ObjectMask object(int coordinate, int extent) {
        ObjectMask object = new ObjectMask(BoundingBoxFactory.uniform3D(coordinate, extent));
        object = object.invert();
        return object;
    }
}
