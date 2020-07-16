/* (C)2020 */
package org.anchoranalysis.math.moment;

import static org.junit.Assert.assertEquals;

import org.anchoranalysis.core.geometry.Vector3d;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.math.rotation.RotationMatrixFromAxisAngleCreator;
import org.junit.Test;

public class SecondMomentMatrixTest {

    @Test
    public void test() {

        RotationMatrixFromAxisAngleCreator rmc =
                new RotationMatrixFromAxisAngleCreator(new Vector3d(-0.866, -0.5, 2.31e-014), 3);
        RotationMatrix rm = rmc.createRotationMatrix();

        double delta = 1e-3;

        // First Row
        assertEquals(0.502414, rm.getMatrix().get(0, 0), delta);
        assertEquals(0.861667, rm.getMatrix().get(0, 1), delta);
        assertEquals(-0.07056, rm.getMatrix().get(0, 2), delta);

        // Second Row
        assertEquals(0.861667, rm.getMatrix().get(1, 0), delta);
        assertEquals(-0.492494, rm.getMatrix().get(1, 1), delta);
        assertEquals(0.122201, rm.getMatrix().get(1, 2), delta);

        // Third Row
        assertEquals(0.07056, rm.getMatrix().get(2, 0), delta);
        assertEquals(-0.12221, rm.getMatrix().get(2, 1), delta);
        assertEquals(-0.98999, rm.getMatrix().get(2, 2), delta);
    }
}
