package org.anchoranalysis.image.voxel.neighborhood;

import static org.junit.Assert.*;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.graph.GraphWithPayload;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFixture;
import org.anchoranalysis.image.object.ObjectMask;
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
