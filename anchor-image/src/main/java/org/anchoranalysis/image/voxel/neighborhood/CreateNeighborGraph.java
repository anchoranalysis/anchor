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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.graph.GraphWithEdgeTypes;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.index.ObjectCollectionRTree;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.neighborhood.EdgeAdder.AddEdge;

/**
 * Creates an undirected graph where each vertex is an object, and edge exists if the objects
 * neighbor
 *
 * @author Owen Feehan
 * @param <V> vertex-type
 */
@RequiredArgsConstructor
public class CreateNeighborGraph<V> {

    /** iff TRUE outputs an undirected graph, otherwise directed */
    private boolean undirected = true;

    private final EdgeAdderParameters edgeAdderParams;

    /**
     * Creates an edge from two neighboring vertices
     *
     * @author Owen Feehan
     * @param <V> vertex-type
     * @param <E> edge-type
     */
    @FunctionalInterface
    public interface EdgeFromVertices<V, E> {
        E createEdge(V v1, V v2, int numberNeighboringPixels);
    }

    /**
     * Create the graph for a given list of vertices
     *
     * @param vertices vertices to construct graph from
     * @param vertexToObject converts the vertex to an object-mask (called repeatedly so should be
     *     low-cost)
     * @param edgeFromVertices creates an edge for two vertices (and the number of neighboring
     *     pixels)
     * @param sceneExtent
     * @param do3D
     * @param <E> edge-type of graph
     * @return the newly created graph
     * @throws CreateException
     */
    public <E> GraphWithEdgeTypes<V, E> createGraph(
            List<V> vertices,
            Function<V, ObjectMask> vertexToObject,
            EdgeFromVertices<V, E> edgeFromVertices,
            Extent sceneExtent,
            boolean do3D)
            throws CreateException {

        // Graph of neighboring objects, with the number of common pixels as an edge
        GraphWithEdgeTypes<V, E> graph = new GraphWithEdgeTypes<>(undirected);

        // Objects from each vertex
        ObjectCollection objects = ObjectCollectionFactory.mapFrom(vertices, vertexToObject::apply);
        checkObjectsInScene(objects, sceneExtent);

        EdgeAdder<V> edgeAdder =
                new EdgeAdder<>(
                        vertices,
                        vertexToObject,
                        new ObjectCollectionRTree(objects),
                        createAndAddEdge(graph, edgeFromVertices),
                        edgeAdderParams);

        for (int i = 0; i < objects.size(); i++) {

            V vertexWith = vertices.get(i);
            graph.addVertex(vertexWith);

            edgeAdder.addEdgesFor(i, objects.get(i), vertexWith, sceneExtent, do3D);
        }

        return graph;
    }

    private static void checkObjectsInScene(ObjectCollection objects, Extent sceneExtent)
            throws CreateException {
        for (ObjectMask objectMask : objects) {
            if (!sceneExtent.contains(objectMask.getBoundingBox())) {
                throw new CreateException(
                        String.format(
                                "Object is not contained (fully or partially) inside scene extent: %s is not in %s",
                                objectMask.getBoundingBox(), sceneExtent));
            }
        }
    }

    private static <V, E> AddEdge<V> createAndAddEdge(
            GraphWithEdgeTypes<V, E> graph, EdgeFromVertices<V, E> edgeFromVertices) {
        return (vertex1, vertex2, numPixels) ->
                graph.addEdge(
                        vertex1, vertex2, edgeFromVertices.createEdge(vertex1, vertex2, numPixels));
    }
}
