package org.anchoranalysis.image.voxel.nghb;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.graph.GraphWithEdgeTypes;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.index.rtree.ObjMaskCollectionRTree;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.image.voxel.nghb.EdgeAdder.AddEdge;

/**
 * 
 * Creates an undirected graph where each vertex is an object, and edge exists if the objects neighbour
 * 
 * @author FEEHANO
 *
 * @param <V> vertex-type
 */
public class CreateNghbGraph<V> {

	// If we have a partition of objects, we don't need to check if objects-intersect as it's not possible by definition (a partition)
	// However, if we have objects that can potentially overlap, we define them 'neighbours' only if objects are adjacent, but don't overlap. In this case, we need to checl
	private boolean preventObjIntersection;
		
	public CreateNghbGraph(boolean preventObjIntersection) {
		super();
		this.preventObjIntersection = preventObjIntersection;
	}
	
	/**
	 * Creates an edge from two neighbouring vertices
	 * 
	 * @author Owen Feehan
	 *
	 * @param <V> vertice-type
	 * @param <E> edge-type
	 */
	@FunctionalInterface
	public interface IEdgeFromVertices<V,E> {
		E createEdge( V v1, V v2, int numNghbPixels );
	}
	
	
	/**
	 * Gets an ObjMask from a vertex
	 * 
	 * @author Owen Feehan
	 *
	 * @param <VertexType>
	 */
	@FunctionalInterface
	public interface IVertexToObjMask<VertexType> {
		ObjectMask objMaskFromVertex( VertexType vt );
	}
	
	/**
	 * Creates a graph with numPixels as the edge type
	 * 
	 * @param vertices
	 * @param vertexToObjMask
	 * @param edgeFromVertices
	 * @param sceneExtnt
	 * @param do3D
	 * @param bigNghb
	 * @param undirected
	 * @param testBothDirs
	 * @return
	 * @throws CreateException
	 */
	public GraphWithEdgeTypes<V,Integer> createGraphWithNumPixels(
			List<V> vertices,
			IVertexToObjMask<V> vertexToObjMask,
			Extent sceneExtnt,
			boolean do3D
		) throws CreateException {
		return createGraph(
			vertices,
			vertexToObjMask,
			(v1, v2, numPixels) -> numPixels,
			sceneExtnt,
			do3D,
			false,
			true,
			false
		);
	}
	
	
	/**
	 * Create the graph for a given list of vertices
	 * 
	 * @param vertices vertices to construct graph from
	 * @param vertexToObjMask converts the vertex to an object-mask (called repeatedly so should be low-cost)
	 * @param edgeFromVertices creates an edge for two vertices (and the number of neighbouring pixels)
	 * @param sceneExtnt
	 * @param do3D
	 * @param <E> edge-type of graph
	 * @param bigNghb iff TRUE uses bigNghb for dilation
	 * @param undirected iff TRUE outputs an undirected graph, otherwise directed
	 * @param testBothDirs iff TRUE each combination of neighbours is tested only once, otherwise twice
	 * @return the newly created graph
	 * @throws CreateException
	 */
	public <E> GraphWithEdgeTypes<V,E> createGraph(
		List<V> vertices,
		IVertexToObjMask<V> vertexToObjMask,
		IEdgeFromVertices<V,E> edgeFromVertices,
		Extent sceneExtnt,
		boolean do3D,
		boolean bigNghb,
		boolean undirected,
		boolean testBothDirs
	) throws CreateException {
		
		// Graph of neighbouring objects, with the number of common pixels as an edge
		GraphWithEdgeTypes<V,E> graph = new GraphWithEdgeTypes<V,E>(undirected);
		
		ObjectCollection objs = objsFromVertices(vertices, vertexToObjMask);
		checkObjsInScene(objs, sceneExtnt);
		ObjMaskCollectionRTree rTree = new ObjMaskCollectionRTree(objs);
				
		EdgeAdder<V> edgeAdder = new EdgeAdder<V>(
			vertices,
			vertexToObjMask,
			rTree,
			createAndAddEdge(graph, edgeFromVertices),
			preventObjIntersection,
			bigNghb,
			testBothDirs
		);
		
		for( int i=0; i<objs.size(); i++) {
			
			ObjectMask om = objs.get(i);
			
			V vertexWith = vertices.get(i);
			graph.addVertex( vertexWith );
			
			edgeAdder.addEdgesFor( i, om, vertexWith, sceneExtnt, do3D );
		}
		
		return graph;
	}
	
	private static void checkObjsInScene( ObjectCollection objs, Extent sceneExtnt ) throws CreateException {
		for( ObjectMask om : objs ) {
			if (!sceneExtnt.contains(om.getBoundingBox())) {
				throw new CreateException(
					String.format(
						"Object is not contained (fully or partially) inside scene extent: %s is not in %s",
						om.getBoundingBox(),
						sceneExtnt
					)
				);
			}
		}
	}
	
	private static <V,E> AddEdge<V> createAndAddEdge(
		GraphWithEdgeTypes<V,E> graph,
		IEdgeFromVertices<V,E> edgeFromVertices
	) {
		return (v1, v2, numPixels) -> graph.addEdge(
			v1,
			v2,
			edgeFromVertices.createEdge(v1, v2, numPixels)
		);
	}
	
	private ObjectCollection objsFromVertices( List<V> vertices, IVertexToObjMask<V> vertexToObjMask ) {
		
		List<ObjectMask> list = vertices.stream().map( v -> 
			vertexToObjMask.objMaskFromVertex(v)
		).collect( Collectors.toList() );
		return new ObjectCollection(list);
	}
	
}
