package org.anchoranalysis.image.bean.spatial.arrange;

import static org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStacks.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.overlay.Overlay;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Overlay}.
 *
 * @author Owen Feehan
 */
class OverlayTest {

    /** Tests aligned towards the <b>minimum</b> on every axis. */
    @Test
    void testMin() throws ArrangeStackException {
        doTest(new Overlay("left", "top", "bottom"), MAGENTA, CYAN, CYAN, false);
    }

    /** Tests aligned towards the <b>center</b> on every axis. */
    @Test
    void testCenter() throws ArrangeStackException {
        doTest(new Overlay("center", "center", "center"), CYAN, MAGENTA, CYAN, false);
    }

    /** Tests aligned towards the <b>max</b> on every axis. */
    @Test
    void testMax() throws ArrangeStackException {
        doTest(new Overlay("right", "bottom", "top"), CYAN, CYAN, MAGENTA, false);
    }

    /** Tests with an overlay with a single z-slice. */
    @Test
    void testRepeatSingleSlice() throws ArrangeStackException {
        doTest(new Overlay("left", "top", "repeat"), MAGENTA, MAGENTA, CYAN, true);
    }

    /** Tests with an overlay with multiple z-slices. */
    @Test
    void testRepeatMultipleSlices() throws ArrangeStackException {
        assertThrows(
                ArrangeStackException.class,
                () -> ColoredDualStacks.combine(new Overlay("right", "bottom", "repeat"), false));
    }

    /**
     * Combines the two stacks, and asserts the expected color of the minimum-corner, center, and
     * the maximum-corner of the big stack.
     */
    private static void doTest(
            StackArranger arranger,
            RGBColor colorExpectedMin,
            RGBColor colorExpectedCenter,
            RGBColor colorExpectedMax,
            boolean flattenSmall)
            throws ArrangeStackException {
        RGBStack combined = ColoredDualStacks.combine(arranger, flattenSmall);
        assertThreePoints(
                "",
                combined,
                new BoundingBox(DualStacks.SIZE_BIG),
                colorExpectedMin,
                colorExpectedCenter,
                colorExpectedMax);
    }
}
