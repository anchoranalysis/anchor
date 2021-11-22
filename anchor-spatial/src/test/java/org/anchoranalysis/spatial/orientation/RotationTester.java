package org.anchoranalysis.spatial.orientation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Tests rotating a point around an angle.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RotationTester {

    private static final double DELTA = 1e-3;

    /**
     * Rotates a point on the X-axis by 90 degrees anti-clockwise in the XY plane.
     *
     * @param orientationRightAngle an orientation that is 90-degrees anti-clockwise from the X-axis
     *     in the XY-plane.
     * @param do3D if true, creates the point to rotate in 3 dimensions, otherwise in 2 dimensions.
     */
    public static void testRightAngleRotation(Orientation orientationRightAngle, boolean do3D) {
        RotationMatrix rotationMatrix = orientationRightAngle.deriveRotationMatrix();
        double[] rotatedPoint = rotationMatrix.rotatePoint(makePoint(1.0, 0.0, do3D));
        assertArrayEquals(makePoint(0.0, 1.0, do3D), rotatedPoint, DELTA);
    }

    private static double[] makePoint(double x, double y, boolean do3D) {
        if (do3D) {
            return new double[] {x, y, 0.0};
        } else {
            return new double[] {x, y};
        }
    }
}
