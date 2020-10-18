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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.graph.GraphWithPayload;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.extent.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NeighborGraph {

    /**
     * Create the graph with object-masks as vertices, where edges represent the number of
     * intersecting voxels between objects.
     *
     * @param objects objects to create a graph of neighbors for, and who become the vertices in the
     *     graph
     * @param sceneExtent
     * @param do3D
     * @return the newly created graph
     * @throws CreateException
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
     * Create the graph with object-masks as vertices, where edges represent the number of
     * intersecting voxels between objects.
     *
     * @param <V> vertex-type from which an object-mask must be derivable
     * @param vertices vertices to construct graph from
     * @param vertexToObject converts the vertex to an object-mask (called repeatedly so should be
     *     low-cost)
     * @param sceneExtent
     * @param do3D
     * @return the newly created graph
     * @throws CreateException
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
