package org.anchoranalysis.spatial.orientation;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Orientation3DEulerAngles}.
 *
 * @author Owen Feehan
 */
class Orientation3DEulerAnglesTest {

    @Test
    void testRotateXYAntiClockwise() {
        Orientation3DEulerAngles orientation = new Orientation3DEulerAngles(0, 0, Math.PI / 2);
        RotationTester.testRightAngleRotation(orientation, true);
    }
}
