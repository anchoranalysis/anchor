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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * Six bounding-boxes distributed over three spatially-overlapping clusters.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ClusteredBoxFixture {

    // START FIRST CLUSTER
    public static final BoundingBox BOX1 = BoundingBoxFactory.uniform3D(10, 10);
    public static final BoundingBox BOX2 = BoundingBoxFactory.uniform3D(20, 20);
    public static final BoundingBox BOX3 = BoundingBoxFactory.uniform3D(15, 10);
    // END FIRST CLUSTER

    // START SECOND CLUSTER
    public static final BoundingBox BOX4 = BoundingBoxFactory.uniform3D(40, 10);
    // END SECOND CLUSTER

    // START THIRD CLUSTER
    public static final BoundingBox BOX5 = BoundingBoxFactory.uniform3D(90, 20);
    public static final BoundingBox BOX6 = BoundingBoxFactory.uniform3D(95, 10);

    // END THIRD CLUSTER

    /**
     * All bounding-boxes as elements in a set, which is not nested.
     *
     * @return the newly-created set.
     */
    public static Set<BoundingBox> allFlat() {
        HashSet<BoundingBox> set = new HashSet<>();
        set.addAll(firstCluster());
        set.addAll(secondCluster());
        set.addAll(thirdCluster());
        return set;
    }

    /**
     * All bounding-boxes as elements in nested sets, for each cluster.
     *
     * @return the newly-created set.
     */
    public static Set<Set<BoundingBox>> allNested() {
        HashSet<Set<BoundingBox>> set = new HashSet<>();
        set.add(firstCluster());
        set.add(secondCluster());
        set.add(thirdCluster());
        return set;
    }

    public static Set<BoundingBox> firstCluster() {
        return new HashSet<>(Arrays.asList(BOX1, BOX2, BOX3));
    }

    public static Set<BoundingBox> secondCluster() {
        return new HashSet<>(Arrays.asList(BOX4));
    }

    public static Set<BoundingBox> thirdCluster() {
        return new HashSet<>(Arrays.asList(BOX5, BOX6));
    }

    public static void addAllClusters(BoundingBoxRTree<BoundingBox> tree) {
        addFirstCluster(tree);
        addSecondCluster(tree);
        addThirdCluster(tree);
    }

    public static void addFirstCluster(BoundingBoxRTree<BoundingBox> tree) {
        add(tree, BOX1);
        add(tree, BOX2);
        add(tree, BOX3);
    }

    private static void addSecondCluster(BoundingBoxRTree<BoundingBox> tree) {
        add(tree, BOX4);
    }

    private static void addThirdCluster(BoundingBoxRTree<BoundingBox> tree) {
        add(tree, BOX5);
        add(tree, BOX6);
    }

    private static void add(BoundingBoxRTree<BoundingBox> tree, BoundingBox box) {
        tree.add(box, box);
    }
}
