package org.anchoranalysis.image.bean.spatial.arrange;

import static org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStacks.*;

import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.arrange.tile.Tile;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Tile}.
 *
 * @author Owen Feehan
 */
class TileTest {

    /**
     * Arranges the big and small stacks in a <b>horizontal row</b> - with fully defined number of
     * rows and columns.
     */
    @Test
    void testHorizontal() throws ArrangeStackException {
        BoundingBox box =
                new BoundingBox(
                        new Point3i(DualStacks.SIZE_BIG.x(), 0, 0),
                        new Extent(
                                DualStacks.SIZE_SMALL.x(),
                                DualStacks.SIZE_BIG.y(),
                                DualStacks.SIZE_BIG.z()));

        doTest(new Tile(2, 1), "leftCell_", "rightCell_", box);
    }

    /**
     * Arranges the big and small stacks in a <b>vertical column</b> - with fully defined number of
     * rows and columns.
     */
    @Test
    void testVertical() throws ArrangeStackException {
        BoundingBox box =
                new BoundingBox(
                        new Point3i(0, DualStacks.SIZE_BIG.y(), 0),
                        new Extent(
                                DualStacks.SIZE_BIG.y(),
                                DualStacks.SIZE_SMALL.y(),
                                DualStacks.SIZE_BIG.z()));

        doTest(new Tile(1, 2), "topCell_", "bottomCell_", box);
    }

    /**
     * Combines the big and small stack, expecting the first cell to be entirely occupied by the big
     * stack, and the small stack to be centered in the second cell.
     *
     * @param arranger how the stacks are arranged.
     * @param prefixFirst assert prefix string for the first cell.
     * @param prefixSecond assert prefix string for the second cell.
     * @param boxSecondCell the bounding-box to use for the second cell.
     */
    private static void doTest(
            StackArranger arranger,
            String prefixFirst,
            String prefixSecond,
            BoundingBox boxSecondCell)
            throws ArrangeStackException {
        RGBStack combined = ColoredDualStacks.combine(arranger, false);

        // The bigger CYAN stack should occupy the first cell
        BoundingBox boxFirstCell = new BoundingBox(DualStacks.SIZE_BIG);
        assertThreePoints(prefixFirst, combined, boxFirstCell, CYAN, CYAN, CYAN);

        // The smaller MAGENTA stack should be centered in the second cell in X and Y, but not in Z.
        assertThreePoints(prefixSecond, combined, boxSecondCell, BLACK, MAGENTA, BLACK);
    }
}
