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
package org.anchoranalysis.spatial.rtree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.base.Functions;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link SpatiallySeparate}.
 *
 * @author Owen Feehan
 */
class SpatiallySeparateTest {

    /** Tests on a small number of objects, whether they form the exact expected clusters. */
    @Test
    void testSmallClustered() {
        List<Set<BoundingBox>> clusters = doTest(ClusteredBoxFixture.allFlat());
        assertEquals(ClusteredBoxFixture.allNested(), new HashSet<>(clusters));
    }

    /** Tests on a small number of objects, whether they form the exact expected clusters. */
    @Test
    void testLargeClustered() {

        // Make a large amount of overlapping bounding boxes, and another set which are disjoint
        List<BoundingBox> boxes = IncrementingBoxFixture.createDisjointIncrementing(100, 20);

        // Add some identical boxes to those that were there previously.
        boxes.addAll(IncrementingBoxFixture.createIncrementingBoxes(5, 0));

        List<Set<BoundingBox>> clusters = doTest(boxes);
        assertEquals(2, clusters.size());
    }

    private List<Set<BoundingBox>> doTest(Collection<BoundingBox> boxes) {
        SpatiallySeparate<BoundingBox> spatiallySeparate =
                new SpatiallySeparate<>(Functions.identity());
        return spatiallySeparate.separate(boxes);
    }
}
