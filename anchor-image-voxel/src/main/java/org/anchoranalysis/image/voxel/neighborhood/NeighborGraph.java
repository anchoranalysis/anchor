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
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.graph.GraphWithPayload;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Creates a graph where each vertex represents an {@link ObjectMask} and edges between indicate that two objects neighbor each other.
 * 
 * <p>The weight associated with the edge, indicates the number of neighbvoring voxels, which is always a positive integer.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NeighborGraph {

    /**
     * Create the graph with object-masks as vertices, where edges represent the number of
     * neighboring voxels between objects.
     *
     * @param objects objects to create a graph of neighbors for, and who become the vertices in the
     *     graph.
     * @param sceneExtent the size of the image, the object-masks exist in.
     * @param preventObjectIntersection iff true, objects can only be neighbors, if they have no intersecting voxels.
     * @param do3D if true, the Z-dimension is also considered for neighbors. Otherwise, only the X and Y dimensions.
     * @return the newly created graph.
     * @throws CreateException if any objects are not fully contained in the scene.
     */
    public static GraphWithPayload<ObjectMask, Integer> create(
            ObjectCollection objects,
            Extent sceneExtent,
            boolean preventObjectIntersection,
            boolean do3D)
            throws CreateException {
        return create(
                objects.asList(),
                Function.identity(),
                sceneExtent,
                preventObjectIntersection,
                do3D);
    }

    /**
     * Like {@link #create(ObjectCollection, Extent, boolean, boolean)} but extracts objects from a
     * list of elements which form the vertices.
     *
     * @param <V> vertex-type from which an object-mask must be derivable.
     * @param vertices the elements to construct graph from, each which maps uniquely to an {@link ObjectMask}.
     * @param vertexToObject converts the vertex to a unique object-mask. This function is called repeatedly so should have
     *     low computational-cost to call.
     * @param sceneExtent the size of the image, the object-masks exist in.
     * @param preventObjectIntersection iff true, objects can only be neighbors, if they have no intersecting voxels.
     * @param do3D if true, the Z-dimension is also considered for neighbors. Otherwise, only the X and Y dimensions.
     * @return the newly created graph.
     * @throws CreateException if any objects are not fully contained in the scene.
     */
    public static <V> GraphWithPayload<V, Integer> create(
            List<V> vertices,
            Function<V, ObjectMask> vertexToObject,
            Extent sceneExtent,
            boolean preventObjectIntersection,
            boolean do3D)
            throws CreateException {
        NeighborGraphCreator<V> creator = new NeighborGraphCreator<>(preventObjectIntersection);
        return creator.createGraphIntersectingVoxels(vertices, vertexToObject, sceneExtent, do3D);
    }
}
