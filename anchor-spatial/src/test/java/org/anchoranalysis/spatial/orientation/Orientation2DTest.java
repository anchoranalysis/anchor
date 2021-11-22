package org.anchoranalysis.spatial.orientation;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Orientation2D}.
 *
 * @author Owen Feehan
 */
class Orientation2DTest {

    @Test
    void testRotateXYAntiClockwise() {
        Orientation2D orientation = new Orientation2D(Math.PI / 2);
        RotationTester.testRightAngleRotation(orientation, false);
    }
}
