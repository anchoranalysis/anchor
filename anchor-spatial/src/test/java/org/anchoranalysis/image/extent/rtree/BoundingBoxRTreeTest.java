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
package org.anchoranalysis.image.extent.rtree;

import static org.anchoranalysis.image.extent.rtree.BoxFixture.*;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.rtree.RTree;
import org.junit.Before;
import org.junit.Test;

public class BoundingBoxRTreeTest {

    private RTree<BoundingBox> tree;

    @Before
    public void before() {
        tree = new RTree<>(7);
        BoxFixture.addFirstCluster(tree);
    }

    /**
     * Tests that the boxes intersect as expected when bounding-boxes intersect, but not when they
     * are exactly adjacent
     */
    @Test
    public void testIntersectsWith() {
        assertIntersectsWith("box1", BOX1, Arrays.asList(BOX1, BOX3));
        assertIntersectsWith("box2", BOX2, Arrays.asList(BOX2, BOX3));
        assertIntersectsWith("box3", BOX3, Arrays.asList(BOX1, BOX2, BOX3));
    }

    private void assertIntersectsWith(String message, BoundingBox box, List<BoundingBox> ids) {
        assertEquals(message, ids, tree.intersectsWith(box));
    }
}
