/* (C)2020 */
package org.anchoranalysis.image.extent;

import static org.junit.Assert.assertTrue;

import org.anchoranalysis.core.geometry.Point3i;
import org.junit.Test;

public class BoundingBoxTest {

    @Test
    public void testIntersect1() {
        // fail("Not yet implemented");

        BoundingBox obj1 = new BoundingBox(new Point3i(154, 58, 3), new Extent(12, 30, 16));
        BoundingBox obj2 = new BoundingBox(new Point3i(46, 62, 5), new Extent(26, 24, 17));

        assertTrue(!obj1.intersection().existsWith(obj2));
    }

    @Test
    public void testIntersect2() {
        // fail("Not yet implemented");

        BoundingBox obj1 = new BoundingBox(new Point3i(0, 0, 0), new Extent(1024, 1024, 1));
        BoundingBox obj2 = new BoundingBox(new Point3i(433, 95, 1), new Extent(1, 1, 1));

        assertTrue(!obj1.intersection().existsWith(obj2));
    }
}
