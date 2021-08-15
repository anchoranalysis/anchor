package org.anchoranalysis.image.voxel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFixture;
import org.anchoranalysis.image.voxel.object.IntersectingObjects;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IntersectingObjects}
 *
 * @author Owen Feehan
 */
class IntersectingObjectsTest {

    /** All objects used in the r-tree. */
    private static final ObjectCollection OBJECTS =
            new ObjectCollectionFixture(1, 3).createObjects(true);

    /** The first object (that intersects with no others) */
    private static final ObjectMask FIRST = OBJECTS.get(0);

    /** The second object (that intersects with one others). */
    private static final ObjectMask SECOND = OBJECTS.get(1);

    /** The third object (that intersects with two others). */
    private static final ObjectMask THIRD = OBJECTS.get(2);

    /** An extent that contains all objects. */
    private static final Extent CONTAINING_EXTENT = new Extent(100, 100, 100);

    /** The r-tree of {@code OBJECTS}, freshly initialized for each test. */
    private IntersectingObjects tree;

    @BeforeEach
    private void init() {
        // We duplicate the original object-list in case the tests involve mutations (removals etc.)
        tree = new IntersectingObjects(OBJECTS.duplicateShallow());
    }

    /** Which objects contain a point? */
    @Test
    void contains() {
        assertContainsOnly(tree.contains(centerOf(FIRST)), FIRST);
        assertEmpty(tree.contains(new Point3i(1, 1, 1)));
    }

    /** Which objects intersect with a particular object? */
    @Test
    void intersectsObject() {
        tree.remove(FIRST, 0);
        tree.remove(SECOND, 1);
        assertContainsOnly(tree.intersectsWith(SECOND), THIRD);
        assertEmpty(tree.intersectsWith(FIRST));
    }

    /**
     * Which objects intersect with a particular bounding-box?
     *
     * @throws OperationFailedException
     */
    @Test
    void intersectsBox() throws OperationFailedException {
        // Test intersection with the exact same bounding-box
        assertContainsOnly(tree.intersectsWith(FIRST.boundingBox()), FIRST);

        // Test intersection with a larger bounding-box that contains only one
        BoundingBox firstGrown = growBy(FIRST.boundingBox(), 1);
        assertContainsOnly(tree.intersectsWith(firstGrown), FIRST);

        // Remove the first item, and verify nothing is found
        tree.remove(FIRST, 0);
        assertEmpty(tree.intersectsWith(FIRST.boundingBox()));
    }

    /** Removes an object from the r-tree. */
    @Test
    void remove() {
        assertSize(4);

        // Remove object with valid index
        tree.remove(FIRST, 0);
        assertSize(3);
    }
    
    /** Separates objects into spatial clusters. */
    @Test
    void spatiallySeparate() {
        Set<ObjectCollection> set = tree.spatiallySeparate();
        Set<Integer> sizesActual = set.stream().map(ObjectCollection::size).collect(Collectors.toSet());
        Set<Integer> sizesExpected = Arrays.asList(1,3).stream().collect(Collectors.toSet()); 
        assertEquals(sizesExpected, sizesActual);
    }

    /** Asserts a particular number of objects in the r-tree. */
    private void assertSize(int expectedSize) {
        assertEquals(expectedSize, tree.size());
    }

    /** The center point of an object. */
    private static Point3i centerOf(ObjectMask object) {
        Point3d center = object.centerOfGravity();
        return PointConverter.intFromDoubleFloor(center);
    }

    /** Increases a bounding-box by {@code toGrowBy} in all dimensions. */
    private static BoundingBox growBy(BoundingBox box, int toGrowBy) {
        return box.growBy(new Point3i(toGrowBy, toGrowBy, toGrowBy), CONTAINING_EXTENT);
    }

    /** Asserts that a collection of objects contains a single object (by deep equivalence). */
    private static void assertContainsOnly(Set<ObjectMask> objects, ObjectMask singleObject) {
        ObjectCollection objectsAsCollection = ObjectCollectionFactory.of(objects);
        assertTrue(objectsAsCollection.equalsDeep(ObjectCollectionFactory.of(singleObject)));
    }

    /** Asserts that a collection of objects contains no elements. */
    private static void assertEmpty(Set<ObjectMask> objects) {
        assertTrue(objects.isEmpty());
    }
}
