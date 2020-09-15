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

import java.util.List;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.box.BoundingBox;

/**
 * An R-Tree that contains items, each with an associated bounding-box.
 *
 * @see <a href="https://en.wikipedia.org/wiki/R-tree">R-tree on Wikipedia</a>
 * @see ObjectCollectionRTree for a related structure operating only on object-masks
 * @param <T> payload-type
 * @author Owen Feehan
 */
public class RTree<T> {

    private static final float[] SINGLE_POINT_EXTENT = new float[] {1, 1, 1};

    private static final int MIN_NUMBER_ENTRIES = 1;

    private static final int NUMBER_DIMENSIONS = 3;

    // We re-use this singlePoint to avoid memory allocation for a single point
    private float[] singlePoint = new float[] {0, 0, 0};

    private final com.newbrightidea.util.RTree<T> tree;

    /**
     * Creates an empty R-Tree.
     *
     * @param maxNumberEntriesSuggested suggested a maximum number of entries in the r-tree
     */
    public RTree(int maxNumberEntriesSuggested) {
        // Insist that maxEntries is at least twice the minimum num items
        int maxNumberEntries = Math.max(maxNumberEntriesSuggested, MIN_NUMBER_ENTRIES * 2);

        tree =
                new com.newbrightidea.util.RTree<>(
                        maxNumberEntries, MIN_NUMBER_ENTRIES, NUMBER_DIMENSIONS);
    }

    /**
     * Adds a bounding-box with a corresponding index.
     *
     * @param box the box to add
     * @param payload the payload associated with the bounding-box
     */
    public void add(BoundingBox box, T payload) {
        float[] coords = minPoint(box);
        float[] dimensions = extent(box);
        tree.insert(coords, dimensions, payload);
    }

    /**
     * Which bounding-boxes contain a particular point.
     *
     * @param point the point
     * @return indices for all bounding-boxes that contain {@code point}.
     */
    public List<T> contains(Point3i point) {
        singlePoint[0] = (float) point.x();
        singlePoint[1] = (float) point.y();
        singlePoint[2] = (float) point.z();

        return tree.search(singlePoint, SINGLE_POINT_EXTENT);
    }

    public List<T> intersectsWith(BoundingBox box) {

        float[] coords = minPoint(box);
        float[] dimensions = extent(box);

        return tree.search(coords, dimensions);
    }

    private static float[] minPoint(BoundingBox box) {
        return new float[] {box.cornerMin().x(), box.cornerMin().y(), box.cornerMin().z()};
    }

    private static float[] extent(BoundingBox box) {
        return new float[] {box.extent().x() - 1, box.extent().y() - 1, box.extent().z() - 1};
    }
}
