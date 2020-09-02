/*-
 * #%L
 * anchor-image
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
package org.anchoranalysis.image.index;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.image.extent.BoundingBoxFixture;
import org.anchoranalysis.image.extent.box.BoundingBox;
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
