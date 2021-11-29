package org.anchoranalysis.image.core.object.scale.method;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/** Creates two rectangular objects, side-by-side. */
class ObjectFixture {

    public static final int WIDTH = 50;

    /** The left-most object that is included in every collection. */
    public static final ObjectMask OBJECT_LEFT = objectAt(0);

    /*** The right-most object that is included only in the <i>adjacent</i> collection. */
    public static final ObjectMask OBJECT_RIGHT_ADJACENT = objectAt(WIDTH);

    /*** The right-most object that is included only in the <i>overlapping</i> collection. */
    public static final ObjectMask OBJECT_RIGHT_OVERLAP = objectAt(WIDTH / 2);

    /**
     * Create a rectangular image where the first half is one object, and the second half is another
     * - without overlap.
     *
     * @return the objects.
     */
    public static List<ObjectMask> objectsAdjacent() {
        return Arrays.asList(OBJECT_LEFT, OBJECT_RIGHT_ADJACENT);
    }

    /**
     * Create a rectangular image where the second object overlaps with the first.
     *
     * @return the objects.
     */
    public static List<ObjectMask> objectsOverlap() {
        return Arrays.asList(OBJECT_LEFT, OBJECT_RIGHT_OVERLAP);
    }

    private static ObjectMask objectAt(int xMin) {
        ObjectMask object =
                new ObjectMask(new BoundingBox(new Point3i(xMin, 0, 0), new Extent(WIDTH, 30, 10)));
        object.assignOn().toAll();
        return object;
    }
}
