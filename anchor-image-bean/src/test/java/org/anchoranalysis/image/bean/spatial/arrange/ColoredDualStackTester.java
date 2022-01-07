package org.anchoranalysis.image.bean.spatial.arrange;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Tests by arranging two {@link RGBStack} of different sizes, the bigger colored entirely in cyan,
 * and the smaller in magenta.
 *
 * @author Owen Feehan
 */
class ColoredDualStackTester {

    /** A {@link RGBColor} that is <b>black</b>. */
    public static final RGBColor BLACK = new RGBColor(Color.BLACK);

    /** A {@link RGBColor} that is <b>cyan</b>. */
    public static final RGBColor CYAN = new RGBColor(Color.CYAN);

    /** A {@link RGBColor} that is <b>magenta</b>. */
    public static final RGBColor MAGENTA = new RGBColor(Color.MAGENTA);

    /**
     * Combines the two stacks, expecting a single-celled output.
     *
     * <p>The expected color of the minimum-corner, center, and the maximum-corner of this cell are
     * asserted.
     *
     * @param arranger how to arrange the two stacks when combined.
     * @param colorExpectedMin the expected color at the <i>minimum corner</i> of the cell.
     * @param colorExpectedCenter the expected color at the <i>center</i> of the cell.
     * @param colorExpectedMax the expected color at the <i>maximum corner</i> of the cell.
     * @param flattenSmall when true, the smaller stack is flattened across the z-dimension.
     * @throws ArrangeStackException if thrown by {@link DualStacks#asList}.
     */
    public static void testSingleCell(
            StackArranger arranger,
            RGBColor colorExpectedMin,
            RGBColor colorExpectedCenter,
            RGBColor colorExpectedMax,
            boolean flattenSmall)
            throws ArrangeStackException {
        RGBStack combined = ColoredDualStackTester.combine(arranger, flattenSmall);
        assertThreePoints(
                "",
                combined,
                new BoundingBox(DualStacks.SIZE_BIG),
                colorExpectedMin,
                colorExpectedCenter,
                colorExpectedMax);
    }

    /**
     * Combines the big and small stack, expecting a two-cell output.
     *
     * <p>The first cell is expected to be entirely occupied by the big stack, and the small stack
     * to be centered in the second cell.
     *
     * @param arranger how the stacks are arranged.
     * @param prefixFirst assert prefix string for the first cell.
     * @param prefixSecond assert prefix string for the second cell.
     * @param boxSecondCell the bounding-box to use for the second cell.
     * @throws ArrangeStackException if thrown by {@link DualStacks#asList}.
     */
    public static void testTwoCells(
            StackArranger arranger,
            String prefixFirst,
            String prefixSecond,
            BoundingBox boxSecondCell)
            throws ArrangeStackException {
        RGBStack combined = ColoredDualStackTester.combine(arranger, false);

        // The bigger CYAN stack should occupy the first cell
        BoundingBox boxFirstCell = new BoundingBox(DualStacks.SIZE_BIG);
        assertThreePoints(prefixFirst, combined, boxFirstCell, CYAN, CYAN, CYAN);

        // The smaller MAGENTA stack should be centered in the second cell in X and Y, but not in Z.
        assertThreePoints(prefixSecond, combined, boxSecondCell, BLACK, MAGENTA, BLACK);
    }

    /**
     * Combines a big cyan-colored image with a smaller magenta-colored image, in that specific
     * order.
     *
     * @param arranger how to arrange the two stacks when combined.
     * @param flattenSmall when true, the smaller stack is flattened across the z-dimension.
     * @return a newly created {@link RGBStack} representing the combination.
     * @throws ArrangeStackException if thrown by {@link DualStacks#asList}.
     */
    public static RGBStack combine(StackArranger arranger, boolean flattenSmall)
            throws ArrangeStackException {
        return arranger.combine(DualStacks.asList(CYAN, MAGENTA, flattenSmall));
    }

    /**
     * Combines the two stacks, and asserts the expected color in {@code stack} at three points.
     *
     * <p>The points that are tested are, respectively:
     *
     * <ul>
     *   <li>the minimum-corner
     *   <li>the center
     *   <li>the maximum-corner
     * </ul>
     *
     * @param messagePrefix a prefix that is placed before the message for each assert (to make the
     *     messages unique if this method is called repeatedly).
     * @param stack the stack containing voxels to be checked.
     * @param box the bounding-box which defines the three points to be checked.
     * @param colorExpectedMin the expected color at the <i>minimum corner</i> of the bounding-box.
     * @param colorExpectedCenter the expected color at the <i>center</i> of the bounding-box.
     * @param colorExpectedMax the expected color at the <i>maximum corner</i> of the bounding-box.
     */
    private static void assertThreePoints(
            String messagePrefix,
            RGBStack stack,
            BoundingBox box,
            RGBColor colorExpectedMin,
            RGBColor colorExpectedCenter,
            RGBColor colorExpectedMax) {
        assertVoxelColor(messagePrefix + "minCorner", stack, box.cornerMin(), colorExpectedMin);
        assertVoxelColor(
                messagePrefix + "center", stack, box.centerOfGravity(), colorExpectedCenter);
        assertVoxelColor(
                messagePrefix + "maxCorner", stack, box.calculateCornerMax(), colorExpectedMax);
    }

    /**
     * Asserts a particular color exists at a voxel.
     *
     * @param message the message if the assert fails.
     * @param stack the stack containing a voxel to be checked.
     * @param point coordinates of the voxel in {@code stack} to check.
     * @param expectedColor the color expected at the voxel.
     */
    private static void assertVoxelColor(
            String message, RGBStack stack, ReadableTuple3i point, RGBColor expectedColor) {
        assertEquals(expectedColor, stack.colorAtVoxel(point), message);
    }
}
