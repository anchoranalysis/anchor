/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.dimensions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point2i;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link OrientationChange}.
 *
 * @author Owen Feehan
 */
class OrientationChangeTest {

    // START: TEST INPUTS
    private static final Extent EXTENT = new Extent(10, 12, 1);

    private static final Extent EXTENT_ROTATED_90 = new Extent(12, 10, 1);

    private static final Point2i POINT = new Point2i(6, 8);
    // END: TEST INPUTS

    // START: EXPECTED TEST RESULTS
    private static final Point2i POINT_ROTATED_90_ANTICLOCKWISE = new Point2i(3, 6);

    private static final Point2i POINT_ROTATED_90_ANTICLOCKWISE_MIRRORED = new Point2i(8, 6);

    private static final Point2i POINT_ROTATED_180 = new Point2i(3, 3);

    private static final Point2i POINT_ROTATED_180_MIRRORED = new Point2i(6, 3);

    private static final Point2i POINT_ROTATED_90_CLOCKWISE = new Point2i(8, 3);

    private static final Point2i POINT_ROTATED_90_CLOCKWISE_MIRRORED = new Point2i(3, 3);
    // END: EXPECTED TEST RESULTS

    @Test
    void noRotation() {
        assertCorrectedIndex(POINT, OrientationChange.KEEP_UNCHANGED, EXTENT.offset(POINT));
    }

    @Test
    void rotate90AntiClockwise() {
        assertCorrectedIndex(
                POINT,
                OrientationChange.ROTATE_90_ANTICLOCKWISE,
                EXTENT_ROTATED_90.offset(POINT_ROTATED_90_ANTICLOCKWISE));
    }

    @Test
    void rotate90AntiClockwiseMirrored() {
        assertCorrectedIndex(
                POINT,
                OrientationChange.ROTATE_90_ANTICLOCKWISE_MIRROR,
                EXTENT_ROTATED_90.offset(POINT_ROTATED_90_ANTICLOCKWISE_MIRRORED));
    }

    @Test
    void rotate180() {
        assertCorrectedIndex(POINT, OrientationChange.ROTATE_180, EXTENT.offset(POINT_ROTATED_180));
    }

    @Test
    void rotate180Mirrored() {
        assertCorrectedIndex(
                POINT,
                OrientationChange.ROTATE_180_MIRROR,
                EXTENT.offset(POINT_ROTATED_180_MIRRORED));
    }

    @Test
    void rotate90Clockwise() {
        assertCorrectedIndex(
                POINT,
                OrientationChange.ROTATE_90_CLOCKWISE,
                EXTENT_ROTATED_90.offset(POINT_ROTATED_90_CLOCKWISE));
    }

    @Test
    void rotate90ClockwiseMirrored() {
        assertCorrectedIndex(
                POINT,
                OrientationChange.ROTATE_90_CLOCKWISE_MIRROR,
                EXTENT_ROTATED_90.offset(POINT_ROTATED_90_CLOCKWISE_MIRRORED));
    }

    private static void assertCorrectedIndex(
            Point2i point, OrientationChange correction, int expectedIndex) {
        int offset = correction.index(point.x(), point.y(), EXTENT);
        assertEquals(expectedIndex, offset);
    }
}
