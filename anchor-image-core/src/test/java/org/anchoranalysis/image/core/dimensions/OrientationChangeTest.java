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

    private static final Extent EXTENT = new Extent(10, 12, 1);

    private static final Extent EXTENT_ROTATED_90 = new Extent(12, 10, 1);

    private static final Point2i POINT = new Point2i(6, 8);

    private static final Point2i POINT_ROTATED_90_CLOCKWISE = new Point2i(3, 6);

    private static final Point2i POINT_ROTATED_180 = new Point2i(3, 3);

    private static final Point2i POINT_ROTATED_270_CLOCKWISE = new Point2i(8, 3);

    @Test
    void noRotation() {
        assertCorrectedIndex(POINT, OrientationChange.KEEP_UNCHANGED, EXTENT.offset(POINT));
    }

    @Test
    void rotate90Clockwise() {
        assertCorrectedIndex(
                POINT,
                OrientationChange.ROTATE_90_ANTICLOCKWISE,
                EXTENT_ROTATED_90.offset(POINT_ROTATED_90_CLOCKWISE));
    }

    @Test
    void rotate180() {
        assertCorrectedIndex(POINT, OrientationChange.ROTATE_180, EXTENT.offset(POINT_ROTATED_180));
    }

    @Test
    void rotate270Clockwise() {
        assertCorrectedIndex(
                POINT,
                OrientationChange.ROTATE_90_CLOCKWISE,
                EXTENT_ROTATED_90.offset(POINT_ROTATED_270_CLOCKWISE));
    }

    private static void assertCorrectedIndex(
            Point2i point, OrientationChange correction, int expectedIndex) {
        int offset = correction.index(point.x(), point.y(), EXTENT);
        assertEquals(expectedIndex, offset);
    }
}
