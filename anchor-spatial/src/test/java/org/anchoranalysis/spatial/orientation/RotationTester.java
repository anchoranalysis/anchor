/*-
 * #%L
 * anchor-spatial
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
