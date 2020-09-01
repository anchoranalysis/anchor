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

import com.google.common.collect.Streams;
import com.newbrightidea.util.RTree;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;

/**
 * An R-Tree of bounding boxes. The index of the item in a list, determines an integer ID,
 * associated with the item in the R-Tree.
 *
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @see ObjectCollectionRTree for a related structure operating only on object-masks
 * @author Owen Feehan
 */
public class BoundingBoxRTree {

    private static final float[] SINGLE_POINT_EXTENT = new float[] {1, 1, 1};

    private static final int MIN_NUMBER_ENTRIES = 1;

    private static final int NUMBER_DIMENSIONS = 3;

    // We re-use this singlePoint to avoid memory allocation for a single point
    private float[] singlePoint = new float[] {0, 0, 0};

    private final RTree<Integer> rTree;

    /**
     * Constructor
     *
     * @param maxNumberEntriesSuggested suggested a maximum number of entries in the r-tree
     */
    public BoundingBoxRTree(int maxNumberEntriesSuggested) {
        // Insist that maxEntries is at least twice the minimum num items
        int maxNumberEntries = Math.max(maxNumberEntriesSuggested, MIN_NUMBER_ENTRIES * 2);

        rTree = new RTree<>(maxNumberEntries, MIN_NUMBER_ENTRIES, NUMBER_DIMENSIONS);
    }

    /**
     * Creates from an initial list of boxes
     *
     * @param boxes added to the r-tree
     * @param maxNumberEntriesSuggested suggested a maximum number of entries in the r-tree
     */
    public BoundingBoxRTree(List<BoundingBox> boxes, int maxNumberEntriesSuggested) {
        this(maxNumberEntriesSuggested);

        // Adds each box to the r-tree with its corresponding index
        IntStream.range(0, boxes.size()).forEach(index -> add(index, boxes.get(index)));
    }

    /**
     * Creates from an initial stream of boxes
     *
     * @param boxes added to the r-tree
     * @param maxNumberEntriesSuggested suggested a maximum number of entries in the r-tree
     */
    public BoundingBoxRTree(Stream<BoundingBox> boxes, int maxNumberEntriesSuggested) {
        this(maxNumberEntriesSuggested);

        // Adds each box to the r-tree with its corresponding index
        Streams.mapWithIndex(
                boxes,
                (box, index) -> {
                    this.add((int) index, box);
                    return 0;
                });
    }

    public List<Integer> contains(Point3i point) {
        singlePoint[0] = (float) point.x();
        singlePoint[1] = (float) point.y();
        singlePoint[2] = (float) point.z();

        return rTree.search(singlePoint, SINGLE_POINT_EXTENT);
    }

    public List<Integer> intersectsWith(BoundingBox box) {

        float[] coords = minPoint(box);
        float[] dimensions = extent(box);

        return rTree.search(coords, dimensions);
    }

    public void add(int i, BoundingBox box) {
        float[] coords = minPoint(box);
        float[] dimensions = extent(box);

        rTree.insert(coords, dimensions, i);
    }

    private static float[] minPoint(BoundingBox box) {
        return new float[] {box.cornerMin().x(), box.cornerMin().y(), box.cornerMin().z()};
    }

    private static float[] extent(BoundingBox box) {
        return new float[] {box.extent().x() - 1, box.extent().y() - 1, box.extent().z() - 1};
    }
}
