/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.spatial.overlay;

import static org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStackTester.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStackTester;
import org.anchoranalysis.image.bean.spatial.arrange.overlay.Overlay;
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
