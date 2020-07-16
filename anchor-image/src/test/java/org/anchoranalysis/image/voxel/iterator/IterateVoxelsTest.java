/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator;

import static org.anchoranalysis.image.voxel.iterator.ObjectMaskFixture.*;
import static org.junit.Assert.*;

import java.util.Optional;
import java.util.function.Consumer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.junit.Test;

public class IterateVoxelsTest {

    /** START: Constants for object sizes and locations */
    private static final int Y_MASK_1 = 30;

    private static final int Y_MASK_2 = 35;
    /** END: Constants for object sizes and locations */

    /** START: Constants for expected results */
    private static final int EXPECTED_SINGLE_NUM_VOXELS_2D = OBJECT_NUM_VOXELS_2D;

    private static final int EXPECTED_INTERSECTION_NUM_VOXELS_2D =
            OBJECT_NUM_VOXELS_2D - ((Y_MASK_2 - Y_MASK_1) * WIDTH);
    private static final int EXPECTED_INTERSECTION_CENTER_X = 39;
    private static final int EXPECTED_INTERSECTION_CENTER_Y = 57;
    /** END: Constants for expected results */
    @Test
    public void test2D() {
        testTwoMasks(
                false,
                EXPECTED_SINGLE_NUM_VOXELS_2D,
                EXPECTED_INTERSECTION_NUM_VOXELS_2D,
                new Point3i(EXPECTED_INTERSECTION_CENTER_X, EXPECTED_INTERSECTION_CENTER_Y, 0));
    }

    @Test
    public void test3D() {
        testTwoMasks(
                true,
                EXPECTED_SINGLE_NUM_VOXELS_2D * DEPTH,
                EXPECTED_INTERSECTION_NUM_VOXELS_2D * DEPTH,
                new Point3i(
                        EXPECTED_INTERSECTION_CENTER_X, EXPECTED_INTERSECTION_CENTER_Y, DEPTH / 2));
    }

    private void testTwoMasks(
            boolean do3D,
            int expectedSingleNumberVoxels,
            int expectedIntersectionNumVoxels,
            Point3i expectedIntersectionCenter) {

        ObjectMaskFixture objectsFixture = new ObjectMaskFixture(do3D);

        ObjectMask mask1 = objectsFixture.filledMask(20, Y_MASK_1);
        ObjectMask mask2 =
                objectsFixture.filledMask(20, Y_MASK_2); // Overlaps with mask1 but not entirely

        testSingleMask("mask1", expectedSingleNumberVoxels, mask1);
        testSingleMask("mask2", expectedSingleNumberVoxels, mask2);
        testIntersectionMasks(
                "intersection",
                expectedIntersectionNumVoxels,
                expectedIntersectionCenter,
                mask1,
                mask2);
        testBoundingBox("bbox1", mask1.getBoundingBox());
        testBoundingBox("bbox2", mask2.getBoundingBox());
    }

    private void testSingleMask(String message, int expectedNumVoxels, ObjectMask mask) {
        testCounter(
                message,
                expectedNumVoxels,
                mask.getBoundingBox().centerOfGravity(),
                counter -> IterateVoxels.callEachPoint(mask, counter));
    }

    private void testIntersectionMasks(
            String message,
            int expectedNumVoxels,
            Point3i expectedCenter,
            ObjectMask mask1,
            ObjectMask mask2) {
        testCounter(
                message,
                expectedNumVoxels,
                expectedCenter,
                counter -> IterateVoxels.overMasks(mask1, Optional.of(mask2), counter));
    }

    private void testBoundingBox(String message, BoundingBox box) {
        testCounter(
                message,
                box.extent().getVolume(),
                box.centerOfGravity(),
                counter -> IterateVoxels.callEachPoint(box, counter));
    }

    private void testCounter(
            String message,
            long expectedNumVoxels,
            Point3i expectedCenter,
            Consumer<AggregatePoints> func) {
        AggregatePoints counter = new AggregatePoints();
        func.accept(counter);
        assertEquals(message + " count", expectedNumVoxels, counter.count());
        assertEquals(message + " center", expectedCenter, counter.center());
    }
}
