/* (C)2020 */
package org.anchoranalysis.math.rotation;

import static org.junit.Assert.*;

import org.anchoranalysis.core.geometry.Vector3d;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RotationMatrixFromAxisAngleCreatorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

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
