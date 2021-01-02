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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.function.Consumer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

class IterateVoxelsTest {

    /** START: Constants for object sizes and locations */
    private static final int Y_MASK_1 = 30;

    private static final int Y_MASK_2 = 35;
    /** END: Constants for object sizes and locations */

    private static final int EXPECTED_INTERSECTION_CENTER_X = 39;
    private static final int EXPECTED_INTERSECTION_CENTER_Y = 57;
    /** END: Constants for expected results */
    @Test
    void test2D() {
        ObjectMaskFixture fixture = new ObjectMaskFixture(true, false);
        testTwoMasks(
                fixture,
                new Point3i(EXPECTED_INTERSECTION_CENTER_X, EXPECTED_INTERSECTION_CENTER_Y, 0));
    }

    @Test
    void test3D() {
        ObjectMaskFixture fixture = new ObjectMaskFixture(true, true);
        testTwoMasks(
                fixture,
                new Point3i(
                        EXPECTED_INTERSECTION_CENTER_X, EXPECTED_INTERSECTION_CENTER_Y, fixture.extent().z() / 2));
    }
    
    /** Expected number of voxels in the intersection. */
    private int expectedIntersection(ObjectMaskFixture fixture) {
        return fixture.expectedVolume() - ((Y_MASK_2 - Y_MASK_1) * fixture.extent().x() * fixture.extent().z());
    }

    private void testTwoMasks(ObjectMaskFixture fixture, Point3i expectedIntersectionCenter) {

        int expectedSingleNumberVoxels = fixture.expectedVolume();
        int expectedIntersectionNumberVoxels = expectedIntersection(fixture);
        
        ObjectMask object1 = fixture.filledMask(20, Y_MASK_1);
        ObjectMask object2 =
                fixture.filledMask(20, Y_MASK_2); // Overlaps with mask1 but not entirely

        testSingleObject("object1", expectedSingleNumberVoxels, object1);
        testSingleObject("object2", expectedSingleNumberVoxels, object2);
        testIntersectionObjects(
                "intersection",
                expectedIntersectionNumberVoxels,
                expectedIntersectionCenter,
                object1,
                object2);
        testBoundingBox("box1", object1.boundingBox());
        testBoundingBox("box2", object2.boundingBox());
    }

    private void testSingleObject(String message, int expectedNumVoxels, ObjectMask object) {
        testCounter(
                message,
                expectedNumVoxels,
                object.boundingBox().centerOfGravity(),
                counter -> IterateVoxelsObjectMask.withPoint(object, counter));
    }

    private void testIntersectionObjects(
            String message,
            int expectedNumVoxels,
            Point3i expectedCenter,
            ObjectMask object1,
            ObjectMask object2) {
        testCounter(
                message,
                expectedNumVoxels,
                expectedCenter,
                counter ->
                        IterateVoxelsObjectMask.withPoint(object1, Optional.of(object2), counter));
    }

    private void testBoundingBox(String message, BoundingBox box) {
        testCounter(
                message,
                box.extent().calculateVolume(),
                box.centerOfGravity(),
                counter -> IterateVoxelsBoundingBox.withPoint(box, counter));
    }

    private void testCounter(
            String message,
            long expectedNumberVoxels,
            Point3i expectedCenter,
            Consumer<AggregatePoints> func) {
        AggregatePoints counter = new AggregatePoints();
        func.accept(counter);
        assertEquals(expectedNumberVoxels, counter.count(), () -> message + " count");
        assertEquals(expectedCenter, counter.center(), () -> message + " center");
    }
}
