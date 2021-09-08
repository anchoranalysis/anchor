/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.image.voxel.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.graph.GraphWithoutPayload;
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

    /** The fixture used to create {@code OBJECTS}. */
    private static final ObjectCollectionFixture OBJECTS_FIXTURE =
            new ObjectCollectionFixture(1, 3);

    /** All objects used in the r-tree. */
    private static final ObjectCollection OBJECTS = OBJECTS_FIXTURE.createObjects(true);

    /** The first object (that intersects with no others) */
    private static final ObjectMask FIRST = OBJECTS.get(0);

    /** The second object (that intersects with one others). */
    private static final ObjectMask SECOND = OBJECTS.get(1);

    /** The third object (that intersects with two others). */
    private static final ObjectMask THIRD = OBJECTS.get(2);

    /** An extent that contains all objects. */
    private static final Extent CONTAINING_EXTENT = new Extent(100, 100, 100);

    /** The r-tree of {@code OBJECTS}, freshly initialized for each test. */
    private IntersectingObjects<ObjectMask> tree;

    @BeforeEach
    private void init() {
        // We duplicate the original object-list in case the tests involve mutations (removals etc.)
        tree = IntersectingObjects.create(OBJECTS.duplicateShallow());
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
        tree.remove(FIRST);
        tree.remove(SECOND);
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
        tree.remove(FIRST);
        assertEmpty(tree.intersectsWith(FIRST.boundingBox()));
    }

    /** Removes an object from the r-tree. */
    @Test
    void remove() {
        assertSize(4);

        // Remove object with valid index
        tree.remove(FIRST);
        assertSize(3);
    }

    /** Separates objects into spatial clusters. */
    @Test
    void spatiallySeparate() {
        Stream<Integer> sizesActual = tree.spatiallySeparate().stream().map(Set::size);
        List<Integer> sizesExpected =
                Arrays.asList(
                        OBJECTS_FIXTURE.getNumberNonOverlapping(),
                        OBJECTS_FIXTURE.getNumberOverlapping());
        assertEquals(toSet(sizesExpected.stream()), toSet(sizesActual));
    }

    /** Builds a graph of the intersecting objects. */
    @Test
    void asGraph() {
        GraphWithoutPayload<ObjectMask> graph = tree.asGraph();
        assertEquals(OBJECTS_FIXTURE.getNumberTotal(), graph.numberVertices());
        assertEquals(2, graph.numberEdges());
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

    /** Converts a stream of elements into a set. */
    private static <T> Set<T> toSet(Stream<T> stream) {
        return stream.collect(Collectors.toSet());
    }
}
