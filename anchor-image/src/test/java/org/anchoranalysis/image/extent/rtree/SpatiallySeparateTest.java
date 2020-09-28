package org.anchoranalysis.image.extent.rtree;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Functions;
import java.util.Set;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.junit.Test;

public class SpatiallySeparateTest {

    @Test
    public void testIntersectsWith() {

        RTree<BoundingBox> tree = new RTree<>(12);
        BoxFixture.addAllClusters(tree);

        SpatiallySeparate<BoundingBox> spatiallySeparate =
                new SpatiallySeparate<>(Functions.identity());

        Set<Set<BoundingBox>> clusters = spatiallySeparate.separateConsume(BoxFixture.allFlat());
        assertEquals(BoxFixture.allNested(), clusters);
    }
}
