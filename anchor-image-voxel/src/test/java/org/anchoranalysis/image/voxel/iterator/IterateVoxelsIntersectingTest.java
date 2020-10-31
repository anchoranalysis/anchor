/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.iterator;

import static org.junit.Assert.*;
import org.anchoranalysis.image.voxel.iterator.intersecting.CountVoxelsIntersectingObjects;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.point.Point2i;
import org.junit.Test;

public class IterateVoxelsIntersectingTest {

    private static final Point2i CORNER = new Point2i(10, 15);

    /** Tests two objects in 2D that overlap a little. */
    @Test
    public void testObjectsOverlap2D() {
        testTwoObjects(1, -1, false);
    }

    /** Tests two objects in 3D that overlap a little. */
    @Test
    public void testObjectsOverlap3D() {
        testTwoObjects(15, -1, true);
    }

    /**
     * Tests two objects in 2D that are exactly adjacent with neither overlap nor distance between
     * them.
     */
    @Test
    public void testObjectsAdjacent2D() {
        testTwoObjects(0, 0, false);
    }

    /**
     * Tests two objects in 3D that are exactly adjacent with neither overlap nor distance between
     * them.
     */
    @Test
    public void testObjectsAdjacent3D() {
        testTwoObjects(0, 0, true);
    }

    /**
     * Applies a test on both the <i>countIntersectingVoxels</i> and <i>hasIntersectingVoxels</i>
     * operations.
     *
     * @param expectedNumberIntersectingVoxels the expected number of voxels that intersect
     * @param shift how much to increase the corner of the second object that is otherwise adjacent
     *     to the first object
     * @param use3D whether to create objects in 3D or 2D
     */
    private void testTwoObjects(int expectedNumberIntersectingVoxels, int shift, boolean use3D) {

        ObjectMaskFixture fixture = new ObjectMaskFixture(false, use3D);
        ObjectMask object1 = fixture.filledMask(CORNER);
        ObjectMask object2 = fixture.filledMask(cornerSecondObject(shift));

        assertEquals(
                expectedNumberIntersectingVoxels,
                CountVoxelsIntersectingObjects.countIntersectingVoxels(object1, object2));
        assertEquals(
                expectedNumberIntersectingVoxels != 0,
                CountVoxelsIntersectingObjects.hasIntersectingVoxels(object1, object2));
    }

    /**
     * Creates a corner for a second object that is adjacent to the side of the first object, with
     * perhaps a shift
     *
     * @param shift how much to increase the corner of the second object
     */
    private Point2i cornerSecondObject(int shift) {
        Point2i extent = new Point2i(ObjectMaskFixture.WIDTH, ObjectMaskFixture.HEIGHT);
        extent.incrementX(shift);
        extent.incrementY(shift);
        return Point2i.immutableAdd(CORNER, extent);
    }
}
