/*-
 * #%L
 * anchor-spatial
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
package org.anchoranalysis.spatial.rtree;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests a {@link BoundingBoxRTree}.
 *
 * @author Owen Feehan
 */
class BoundingBoxRTreeTest {

    /** First bounding box that doesn't intersect with any other. */
    private static final BoundingBox BOX1 = BoundingBoxFactory.at(3, 4, 5, 6, 7, 8);

    /** Second bounding box that intersects with {@code BOX3}. */
    private static final BoundingBox BOX2 = BoundingBoxFactory.at(13, 14, 15, 6, 7, 8);

    /** Third bounding box that intersects with {@code BOX2} */
    private static final BoundingBox BOX3 = BoundingBoxFactory.at(15, 16, 17, 6, 7, 8);

    /**
     * An additional box that may be queried or added, and which intersects with {@code BOX2} and
     * {@code BOX3}.
     */
    private static final BoundingBox BOX_ADDITIONAL = BoundingBoxFactory.at(14, 15, 17, 6, 7, 8);

    /** A bounding box that intersects with no other. */
    private static final BoundingBox BOX_WITHOUT_INTERSECTION =
            BoundingBoxFactory.at(1, 1, 1, 1, 1, 1);

    /** The r-tree freshly created and intialized for each test. */
    private BoundingBoxRTree<Integer> tree;

    /** Performs asserts on {@code tree}. */
    private RTreeChecker checker;

    /** Sets up an r-tree before each test using 1, 2 and 3 as payloads for the respective boxes. */
    @BeforeEach
    void initialize() {
        tree = new BoundingBoxRTree<>(3);
        tree.add(BOX1, 1);
        tree.add(BOX2, 2);
        tree.add(BOX3, 3);
        checker = new RTreeChecker(tree);
    }

    @Test
    void contains() {
        assertContains(new Point3i(), Arrays.asList());
        assertContains(cornerPlusOne(BOX1), Arrays.asList(1));
        assertContains(cornerPlusOne(BOX3), Arrays.asList(2, 3));
    }

    @Test
    void intersectsWith() {
        assertIntersectsWith(BOX_WITHOUT_INTERSECTION, Arrays.asList());
        assertIntersectsWith(BOX1, Arrays.asList(1));
        assertIntersectsWith(BOX3, Arrays.asList(2, 3));
        assertIntersectsWith(BOX_ADDITIONAL, Arrays.asList(2, 3));
    }

    @Test
    void add() {
        // Initial state
        assertContainsSize(BOX3, Arrays.asList(2, 3), 3);

        // Add a box that intersects with box 3
        tree.add(BOX_ADDITIONAL, 4);
        assertContainsSize(BOX3, Arrays.asList(2, 3, 4), 4);

        // Add a box that already exists but with a different payload
        tree.add(BOX_ADDITIONAL, 5);
        assertContainsSize(BOX3, Arrays.asList(2, 3, 4, 5), 5);

        // Add a box that already exists but with an existing payload
        tree.add(BOX_ADDITIONAL, 5);
        assertContainsSize(BOX3, Arrays.asList(2, 3, 4, 5), 6);
    }

    @Test
    void remove() {
        checker.assertSize(3);

        // Remove an bounding-box that exists with correct payload
        tree.remove(BOX2, 2);
        checker.assertSize(2);

        // Remove a bounding-box that doesn't exist
        tree.remove(BOX_ADDITIONAL, 5);
        checker.assertSize(2);

        // Adds an additional box where BOX1 already exists, so there are two (with different
        // payloads)
        tree.add(BOX1, 6);

        // Removes the first box with correct payload
        tree.remove(BOX1, 1);
        checker.assertSize(2);

        // Removes the second box with correct payload
        tree.remove(BOX1, 6);
        checker.assertSize(1);
    }

    /** Assert that particular payloads are expected to intersect with a box. */
    private void assertIntersectsWith(BoundingBox box, List<Integer> expectedIdentifiers) {
        RTreeChecker.assertUnordered(expectedIdentifiers, tree.intersectsWith(box));
    }

    /** Assert that particular payloads are expected at a given point. */
    private void assertContains(ReadableTuple3i point, List<Integer> expectedIdentifiers) {
        RTreeChecker.assertUnordered(expectedIdentifiers, tree.contains(point));
    }

    /**
     * Asserts that the minimum corner of a bounding-box contains particular identifiers, and the
     * total size of the r-tree is as expected.
     *
     * @param box the box whose minimum corner is used as point to query (what it contains)
     * @param expectedIdentifiers the identifiers we expect when we see what is contained at the
     *     point above
     * @param expectedSize the expected total number of elements in the r-tree.
     */
    private void assertContainsSize(
            BoundingBox box, List<Integer> expectedIdentifiers, int expectedSize) {
        assertContains(box.cornerMin(), expectedIdentifiers);
        checker.assertSize(expectedSize);
    }

    /** The minimum corner of a bounding box, with 1 added in each dimension. */
    private static Point3i cornerPlusOne(BoundingBox box) {
        return Point3i.immutableAdd(box.cornerMin(), new Point3i(1, 1, 1));
    }
}
