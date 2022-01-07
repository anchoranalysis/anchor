package org.anchoranalysis.image.bean.spatial.arrange;

import static org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStackTester.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.overlay.Overlay;
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
        testSingleCell(new Overlay("left", "top", "bottom"), MAGENTA, MAGENTA, CYAN, false);
    }

    /** Tests aligned towards the <b>max</b> on every axis. */
    @Test
    void testMax() throws ArrangeStackException {
        testSingleCell(new Overlay("right", "bottom", "top"), CYAN, MAGENTA, MAGENTA, false);
    }
    
    /** Tests aligned towards the <b>center</b> on every axis. */
    @Test
    void testCenter() throws ArrangeStackException {
        testSingleCell(new Overlay("center", "center", "center"), CYAN, MAGENTA, CYAN, false);
    }

    /** Tests with an overlay with a single z-slice. */
    @Test
    void testRepeatSingleSlice() throws ArrangeStackException {
        testSingleCell(new Overlay("left", "top", "repeat"), MAGENTA, MAGENTA, CYAN, true);
    }

    /** Tests with an overlay with multiple z-slices. */
    @Test
    void testRepeatMultipleSlices() throws ArrangeStackException {
        assertThrows(
                ArrangeStackException.class,
                () ->
                        ColoredDualStackTester.combine(
                                new Overlay("right", "bottom", "repeat"), false));
    }
}
