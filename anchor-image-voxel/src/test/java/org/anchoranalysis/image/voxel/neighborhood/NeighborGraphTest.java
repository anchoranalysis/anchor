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
package org.anchoranalysis.image.voxel.neighborhood;

import static org.junit.Assert.*;

import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.graph.GraphWithPayload;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFixture;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.junit.Test;

public class NeighborGraphTest {

    @Test
    public void testAdjacent() throws CreateException {
        ObjectCollectionFixture fixture = new ObjectCollectionFixture(5, 0, 0, false);
        testBoth2DAnd3D(fixture.getNumberNonOverlappingObjects() - 1, false, fixture);
        testBoth2DAnd3D(fixture.getNumberNonOverlappingObjects() - 1, true, fixture);
    }

    @Test
    public void testOverlapping() throws CreateException {
        ObjectCollectionFixture fixture = new ObjectCollectionFixture();
        testBoth2DAnd3D(fixture.getNumberOverlappingObjects() - 1, false, fixture);
        testBoth2DAnd3D(0, true, fixture);
    }

    private void testBoth2DAnd3D(
            int expectedNumberEdges,
            boolean preventObjectIntersection,
            ObjectCollectionFixture fixture)
            throws CreateException {
        String prefix =
                preventObjectIntersection ? "preventObjectIntersection" : "allowObjectIntersection";
        test(prefix, expectedNumberEdges, preventObjectIntersection, false, fixture);
        test(prefix, expectedNumberEdges, preventObjectIntersection, true, fixture);
    }

    private void test(
            String prefix,
            int expectedNumberEdges,
            boolean preventObjectIntersection,
            boolean do3D,
            ObjectCollectionFixture fixture)
            throws CreateException {

        fixture.setDo3D(do3D);
        ObjectCollection objects = fixture.createObjects(false);
        GraphWithPayload<ObjectMask, Integer> graph =
                NeighborGraph.create(
                        objects,
                        fixture.extentLargerThanAllObjects(),
                        preventObjectIntersection,
                        do3D);

        String prefixAugmented = prefix + (do3D ? "_3D_" : "_2D_");
        assertEquals(prefixAugmented + "numberVertices", objects.size(), graph.numberVertices());
        assertEquals(prefixAugmented + "numberEdges", expectedNumberEdges, graph.numberEdges());
    }
}
