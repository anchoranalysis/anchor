package org.anchoranalysis.image.index;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.image.BoundingBoxFixture;
import org.anchoranalysis.image.extent.BoundingBox;
import org.junit.Before;
import org.junit.Test;

public class BoundingBoxRTreeTest {

    private static final BoundingBox BOX1 = BoundingBoxFixture.of(10, 10);
    private static final BoundingBox BOX2 = BoundingBoxFixture.of(20, 20);
    private static final BoundingBox BOX3 = BoundingBoxFixture.of(15, 10);

    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID3 = 3;

    private BoundingBoxRTree rtree;

    @Before
    public void before() {
        rtree = new BoundingBoxRTree(200);
        rtree.add(ID1, BOX1);
        rtree.add(ID2, BOX2);
        rtree.add(ID3, BOX3);
    }

    /**
     * Tests that the boxes intersect as expected when bounding-boxes intersect, but not when they
     * are exactly ajcacent
     */
    @Test
    public void testIntersectsWith() {
        assertIntersectsWith("box1", BOX1, Arrays.asList(ID1, ID3));
        assertIntersectsWith("box2", BOX2, Arrays.asList(ID2, ID3));
        assertIntersectsWith("box3", BOX3, Arrays.asList(ID1, ID2, ID3));
    }

    private void assertIntersectsWith(String message, BoundingBox box, List<Integer> ids) {
        assertEquals(message, ids, rtree.intersectsWith(box));
    }
}
