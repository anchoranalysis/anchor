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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests a {@link BoundingBoxRTree}.
 *
 * @author Owen Feehan
 */
class IntervalRTreeTest {

    /** The r-tree freshly created and intialized for each test. */
    private IntervalRTree<Integer> tree;

    /** Performs asserts on {@code tree}. */
    private RTreeChecker checker;

    /**
     * Sets up an r-tree before each test using 1, 2 and 3 as payloads for the respective intervals.
     */
    @BeforeEach
    void initialize() {
        tree = new IntervalRTree<>(3);
        tree.add(1.0, 3.0, 1);
        tree.add(2.0, 5.0, 2);
        tree.add(5.0, 8.0, 3);
        checker = new RTreeChecker(tree);
    }

    @Test
    void contains() {
        assertContains(0.5, Arrays.asList());
        assertContains(1.2, Arrays.asList(1));
        assertContains(3.0, Arrays.asList(1, 2));
        assertContains(5.0, Arrays.asList(2, 3));
    }

    @Test
    void intersectsWith() {
        assertIntersectsWith(0.1, 0.99, Arrays.asList());
        assertIntersectsWith(0.1, 1.0, Arrays.asList(1));
        assertIntersectsWith(0.5, 8.5, Arrays.asList(1, 2, 3));
    }

    @Test
    void add() {
        // Initial state
        assertContainsSize(3.0, Arrays.asList(1, 2), 3);

        // Add a box that intersects with box 3
        tree.add(2.9, 3.1, 4);
        assertContainsSize(3.0, Arrays.asList(1, 2, 4), 4);
    }

    @Test
    void remove() {
        checker.assertSize(3);

        // Remove an bounding-box that exists with correct payload
        tree.remove(2.0, 5.0, 2);
        checker.assertSize(2);

        // Remove a bounding-box that doesn't exist
        tree.remove(4.4, 5.5, 5);
        checker.assertSize(2);
    }

    /**
     * Asserts that the tree contains particular identifiers at {@code point}, and the total size of
     * the r-tree is as expected.
     *
     * @param value the point that is used query what three contains.
     * @param expectedIdentifiers the identifiers we expect when we see what is contained at the
     *     point above
     * @param expectedSize the expected total number of elements in the r-tree.
     */
    private void assertContainsSize(
            double value, List<Integer> expectedIdentifiers, int expectedSize) {
        assertContains(value, expectedIdentifiers);
        checker.assertSize(expectedSize);
    }

    /** Assert that particular payloads are expected to intersect with an interval. */
    private void assertIntersectsWith(double min, double max, List<Integer> expectedIdentifiers) {
        RTreeChecker.assertUnordered(expectedIdentifiers, tree.intersectsWith(min, max));
    }

    /** Assert that particular payloads are expected at a given point. */
    private void assertContains(double point, List<Integer> expectedIdentifiers) {
        RTreeChecker.assertUnordered(expectedIdentifiers, tree.contains(point));
    }
}
