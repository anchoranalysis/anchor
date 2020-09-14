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
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.index.ObjectCollectionRTree;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.morphological.MorphologicalDilation;

/**
 * Adds edges if objects neighbor each other
 *
 * @author Owen Feehan
 * @param <V> vertex-type
 */
@RequiredArgsConstructor
class EdgeAdder<V> {

    // START REQUIRED ARGUMENTS
    /** a list of vertices */
    private final List<V> verticesAsList;

    /** how to convert a individual vertex to an object-mask */
    private final Function<V, ObjectMask> vertexToObject;

    /** the rTree underpinning the vertices (or rather their derived object-masks) */
    private final ObjectCollectionRTree rTree;

    private final AddEdge<V> addEdge;

    /** avoids any edge if any two objects have a common pixel */
    private final EdgeAdderParameters params;
    // END REQUIRED ARGUMENTS

    @FunctionalInterface
    public static interface AddEdge<V> {
        void addEdge(V src, V dest, int numBorderPixels);
    }

    public void addEdgesFor(
            int ignoreIndex, ObjectMask object, V vertexWith, Extent sceneExtent, boolean do3D)
            throws CreateException {

        ObjectMask dilated =
                MorphologicalDilation.createDilatedObject(
                        object,
                        Optional.of(sceneExtent),
                        do3D && sceneExtent.z() > 1,
                        1,
                        params.isBigNeighborhood());

        addWithDilatedMask(ignoreIndex, object, vertexWith, dilated);
    }

    private void addWithDilatedMask(
            int ignoreIndex, ObjectMask object, V vertexWith, ObjectMask dilated) {
        List<Integer> indicesIntersects = rTree.intersectsWithAsIndices(dilated.boundingBox());
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
        if (params.isTestBothDirections()) {
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
        if (params.isPreventObjectIntersection() && object.hasIntersectingVoxels(other)) {
            return;
        }

        // How many border pixels shared between the two?
        int numBorderPixels = numBorderPixels(dilated, other);
        if (numBorderPixels > 0) {
            addEdge.addEdge(vertexWith, vertexOther, numBorderPixels);
        }
    }

    private static int numBorderPixels(ObjectMask object1Dilated, ObjectMask object2) {
        return object1Dilated.countIntersectingVoxels(object2);
    }
}
