/*-
 * #%L
 * anchor-math
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.spatial.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.spatial.point.Vector3d;
import org.anchoranalysis.spatial.rotation.factory.RotateAxisAngle;
import org.junit.jupiter.api.Test;

class SecondMomentMatrixTest {

    @Test
    void test() {

        RotateAxisAngle angleCreator =
                new RotateAxisAngle(new Vector3d(-0.866, -0.5, 2.31e-014), 3);
        RotationMatrix rotationMatrix = angleCreator.create();

        double delta = 1e-3;

        // First Row
        assertEquals(0.502414, rotationMatrix.getMatrix().get(0, 0), delta);
        assertEquals(0.861667, rotationMatrix.getMatrix().get(0, 1), delta);
        assertEquals(-0.07056, rotationMatrix.getMatrix().get(0, 2), delta);

        // Second Row
        assertEquals(0.861667, rotationMatrix.getMatrix().get(1, 0), delta);
        assertEquals(-0.492494, rotationMatrix.getMatrix().get(1, 1), delta);
        assertEquals(0.122201, rotationMatrix.getMatrix().get(1, 2), delta);

        // Third Row
        assertEquals(0.07056, rotationMatrix.getMatrix().get(2, 0), delta);
        assertEquals(-0.12221, rotationMatrix.getMatrix().get(2, 1), delta);
        assertEquals(-0.98999, rotationMatrix.getMatrix().get(2, 2), delta);
    }
}
