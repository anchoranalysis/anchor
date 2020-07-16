/* (C)2020 */
package org.anchoranalysis.core.geometry;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {

    @Test
    public void testEquals() {

        Point2d point2d = new Point2d(-3.1, 4.2);
        assertTrue(point2d.equals(new Point2d(-3.1, 4.2)));

        Point2f point2f = new Point2f(-3.1f, 4.2f);
        assertTrue(point2f.equals(new Point2f(-3.1f, 4.2f)));

        // At present, different types are not allowed be equal
        assertTrue(point2f.equals(point2d.toFloat()));

        Point2i point2i = new Point2i(-3, 4);
        assertTrue(point2i.equals(new Point2i(-3, 4)));

        Point3d point3d = new Point3d(-3.1, 4.2, 6.3);
        assertTrue(point3d.equals(new Point3d(-3.1, 4.2, 6.3)));

        Point3f point3f = new Point3f(-3.1f, 4.2f, 6.3f);
        assertTrue(point3f.equals(new Point3f(-3.1f, 4.2f, 6.3f)));

        assertTrue(point3f.equals(PointConverter.floatFromDouble(point3d)));

        Point3i point3i = new Point3i(-3, 4, 6);
        assertTrue(point3i.equals(new Point3i(-3, 4, 6)));
    }
}
