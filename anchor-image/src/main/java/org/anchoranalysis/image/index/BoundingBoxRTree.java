/* (C)2020 */
package org.anchoranalysis.image.index;

import com.newbrightidea.util.RTree;
import java.util.List;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;

/**
 * An R-Tree of bounding boxes. The index of the item in a list, determines an integer ID,
 * associated with the item in the R-Tree.
 *
 * @author Owen Feehan
 */
public class BoundingBoxRTree {

    private RTree<Integer> rTree;

    // We re-use this singlePoint to avoid memory allocation for a single point
    private float[] singlePoint = new float[] {0, 0, 0};

    private float[] singlePointExtent = new float[] {1, 1, 1};

    public BoundingBoxRTree(int maxEntriesSuggested) {
        // We insist that maxEntries is at least twice the minimum num items
        int minEntries = 1;
        int maxEntries = Math.max(maxEntriesSuggested, minEntries * 2);

        rTree = new RTree<>(maxEntries, minEntries, 3);
    }

    public BoundingBoxRTree(List<BoundingBox> bboxList, int maxEntriesSuggested) {
        this(maxEntriesSuggested);

        for (int i = 0; i < bboxList.size(); i++) {
            add(i, bboxList.get(i));
        }
    }

    public List<Integer> contains(Point3i point) {
        singlePoint[0] = (float) point.getX();
        singlePoint[1] = (float) point.getY();
        singlePoint[2] = (float) point.getZ();

        return rTree.search(singlePoint, singlePointExtent);
    }

    public List<Integer> intersectsWith(BoundingBox bbox) {

        float[] coords = minPoint(bbox);
        float[] dimensions = extent(bbox);

        return rTree.search(coords, dimensions);
    }

    public void add(int i, BoundingBox bbox) {
        float[] coords = minPoint(bbox);
        float[] dimensions = extent(bbox);

        rTree.insert(coords, dimensions, i);
    }

    private static float[] minPoint(BoundingBox bbox) {
        return new float[] {
            bbox.cornerMin().getX(), bbox.cornerMin().getY(), bbox.cornerMin().getZ()
        };
    }

    private static float[] extent(BoundingBox bbox) {
        return new float[] {bbox.extent().getX(), bbox.extent().getY(), bbox.extent().getZ()};
    }
}
