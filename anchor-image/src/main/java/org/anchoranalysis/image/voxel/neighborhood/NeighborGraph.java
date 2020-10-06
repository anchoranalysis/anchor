package org.anchoranalysis.image.voxel.neighborhood;

import java.util.List;
import java.util.function.Function;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.graph.GraphWithPayload;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class NeighborGraph {

    /**
     * Create the graph with object-masks as vertices, where edges represent the number of intersecting voxels between objects.
     *
     * @param objects objects to create a graph of neighbors for, and who become the vertices in the graph
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
        return create(objects.asList(), Function.identity(), sceneExtent, preventObjectIntersection, do3D);
    }
    
    /**
     * Create the graph with object-masks as vertices, where edges represent the number of intersecting voxels between objects.
     *
     * @param <V> vertex-type from which an object-mask must be derivable
     * @param vertices vertices to construct graph from
     * @param vertexToObject converts the vertex to an object-mask (called repeatedly so should be low-cost)
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
