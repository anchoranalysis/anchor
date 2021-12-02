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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.morphological.MorphologicalDilation;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.rtree.BoundingBoxRTree;

/**
 * Adds edges if objects neighbor each other.
 *
 * @author Owen Feehan
 * @param <V> vertex-type
 */
class EdgeAdder<V> {

    // START REQUIRED ARGUMENTS
    /** A list of vertices */
    private final List<V> verticesAsList;

    /** How to convert a individual vertex to an object-mask. */
    private final Function<V, ObjectMask> vertexToObject;

    private final AddEdge<V> addEdge;

    /** Avoids any edge if any two objects have a common pixel. */
    private final EdgeAdderParameters parameters;
    // END REQUIRED ARGUMENTS

    /** The r-tree underpinning the vertices (or rather their derived object-masks) */
    private final BoundingBoxRTree<Integer> rTree;

    /**
     * Create extract objects from a vertex.
     *
     * @param verticesAsList a list of vertices.
     * @param vertexToObject how to convert a individual vertex to an object-mask.
     * @param objects the objects represented in the graph.
     * @param addEdge
     * @param parameters avoids any edge if any two objects have a common pixel.
     */
    public EdgeAdder(
            List<V> verticesAsList,
            Function<V, ObjectMask> vertexToObject,
            ObjectCollection objects,
            AddEdge<V> addEdge,
            EdgeAdderParameters parameters) {
        this.verticesAsList = verticesAsList;
        this.vertexToObject = vertexToObject;
        this.addEdge = addEdge;
        this.parameters = parameters;
        this.rTree = createIndicesRTree(objects);
    }

    @FunctionalInterface
    public static interface AddEdge<V> {
        void addEdge(V src, V dest, int numBorderPixels);
    }

    public void addEdgesFor(
            int ignoreIndex, ObjectMask object, V vertexWith, Extent sceneExtent, boolean do3D)
            throws CreateException {

        ObjectMask dilated =
                MorphologicalDilation.dilate(
                        object,
                        Optional.of(sceneExtent),
                        do3D && sceneExtent.z() > 1,
                        1,
                        parameters.isBigNeighborhood());

        addWithDilatedMask(ignoreIndex, object, vertexWith, dilated);
    }

    private void addWithDilatedMask(
            int ignoreIndex, ObjectMask object, V vertexWith, ObjectMask dilated) {
        Set<Integer> indicesIntersects = rTree.intersectsWith(dilated.boundingBox());
        for (int j : indicesIntersects) {

            // We enforce an ordering, so as not to do the same pair twice (or the identity case)
            if (doSkipIndex(j, ignoreIndex)) {
                continue;
            }

            V vertexOther = verticesAsList.get(j);

            maybeAddEdge(
                    object,
                    dilated,
                    vertexToObject.apply(vertexOther),
                    vertexWith,
                    verticesAsList.get(j));
        }
    }

    private boolean doSkipIndex(int index, int ignoreIndex) {
        if (parameters.isTestBothDirections()) {
            if (index == ignoreIndex) {
                return true;
            }
        } else {
            if (index >= ignoreIndex) {
                return true;
            }
        }
        return false;
    }

    private void maybeAddEdge(
            ObjectMask object, ObjectMask dilated, ObjectMask other, V vertexWith, V vertexOther) {
        // Check that they don't overlap
        if (parameters.isPreventObjectIntersection() && object.hasIntersectingVoxels(other)) {
            return;
        }

        // How many border pixels shared between the two?
        int numberSharedVoxels = numberBorderVoxels(dilated, other);
        if (numberSharedVoxels > 0) {
            addEdge.addEdge(vertexWith, vertexOther, numberSharedVoxels);
        }
    }

    /** Creates an r-tree mapping the bounding-box of objects to their index in a collection. */
    private static BoundingBoxRTree<Integer> createIndicesRTree(ObjectCollection objects) {
        BoundingBoxRTree<Integer> tree = new BoundingBoxRTree<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            tree.add(objects.get(i).boundingBox(), i);
        }
        return tree;
    }

    private static int numberBorderVoxels(ObjectMask object1Dilated, ObjectMask object2) {
        return object1Dilated.countIntersectingVoxels(object2);
    }
}
