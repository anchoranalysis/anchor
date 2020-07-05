package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.graph.GraphWithEdgeTypes;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy.MergeCandidate;
import org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy.MergeStrategy;

/**
 * A graph with:
 * 
 * Each ContiguousPixelPath is a vertex
 * An edge with a cost exists, if there is a connPoint in the respective ContigousPixelPath entry
 * 
 * The edges are directed from connPnt (previous path) to initialPnt (destination path)
 * 
 * @author FEEHANO
 *
 */
public class MergePathGraphConnPoint {

	private GraphWithEdgeTypes<ContiguousPixelPath,PotentialMerge> graph;
	
	private static class PotentialMerge{

		private MergeTarget target;
		private MergeStrategy mergeStrategy;
		
		public PotentialMerge(MergeTarget src, MergeTarget target) {
			super();
			this.target = target;
			
			MergeCandidate mergeCandidate = new MergeCandidate(
				src.getPath(),
				target.getPath(),
				src.mergePnt(),
				target.mergePnt()
			);
			mergeStrategy = mergeCandidate.determineCost();
		}
		
		@Override
		public String toString() {
			return String.format("target=%s (conn=%s, mergeStrategy=%s)", target.getPath().head(), target.mergePnt(), mergeStrategy );
		}
	}
	
	private static class PathIndex {
		
		private ContiguousPixelPath path;
		private int index;
		
		public PathIndex(ContiguousPixelPath path, int index) {
			super();
			this.path = path;
			this.index = index;
		}

		public ContiguousPixelPath getPath() {
			return path;
		}

		public int getIndex() {
			return index;
		}
	}
	
	private static class PixelMapToPath {
		
		private Map<Point3i,PathIndex> map = new HashMap<>();
		
		public PixelMapToPath( List<ContiguousPixelPath> vertices ) {
			for( ContiguousPixelPath cpp : vertices ) {
				addPntsFrom( cpp );
			}
		}
		
		// Null if no path exists
		public PathIndex pathFor( Point3i pnt ) {
			return map.get( pnt );
		}
		
		private void addPntsFrom( ContiguousPixelPath cpp ) {
			for( int i=0; i<cpp.size(); i++ ) {
				map.put(
					cpp.get(i),
					new PathIndex(cpp,i)
				);
			}
		}
	}
	
	
	/**
	 * Constructs a MergeGraph by building edges between the vertices if they
	 *   are neighbour each other (big neighbourhood: 8 instead of 4 )
	 *   
	 * @param vertices
	 * @param maxDist
	 * @throws CreateException 
	 */
	public MergePathGraphConnPoint( List<ContiguousPixelPath> vertices, Extent sceneExtnt ) throws CreateException {
		
		// Graph of neighbouring objects, with the number of common pixels as an edge
		graph = new GraphWithEdgeTypes<>( true );

		addVertices( vertices );
		addEdges( vertices );
						
		
		
		ContiguousPixelPath maxPath = findMaxSize(vertices);
		System.out.printf("MaxPath = %s%n", maxPath);
		//findAllCycles(maxPath);
		
		// Remove all vertices that have only a single edge
		{
			List<ContiguousPixelPath> toRemove = new ArrayList<>();
			for( ContiguousPixelPath cpp : vertices) {
				if( graph.adjacentVertices(cpp).size()==1) {
					toRemove.add(cpp);
				}
			}
			
			for( ContiguousPixelPath cpp : toRemove) {
				graph.removeVertex(cpp);
			}
		}
		
		//
		
		System.out.println(graph);
	}
	
	private void addVertices( List<ContiguousPixelPath> vertices ) {
		
		for( ContiguousPixelPath cpp : vertices ) {
			graph.addVertex( cpp );
		}		
	}
	
	private void addEdges( List<ContiguousPixelPath> vertices ) {
		PixelMapToPath pixelMap = new PixelMapToPath(vertices);
		for( int i=0; i<vertices.size(); i++ ) {
			ContiguousPixelPath cpp = vertices.get(i);
		
			if (cpp.getInitialPnt()!=null && cpp.getConnPnt()!=null) {
				addEdge(cpp, i, pixelMap);
			}
		}
	}
	
	private void addEdge( ContiguousPixelPath cpp, int index, PixelMapToPath pixelMap ) {
		// Find the other path that has the connection point
		PathIndex conn = pixelMap.pathFor( cpp.getConnPnt() );
		
		MergeTarget src = new MergeTarget(
			conn.getPath(),
			conn.getIndex(),
			conn.getPath().points().indexOf( cpp.getConnPnt() )
		);
		
		MergeTarget target = new MergeTarget(
			cpp,
			index,
			cpp.indexInitialPnt()
		);
		
		graph.addEdge(
			conn.getPath(),
			cpp,
			new PotentialMerge(src,target)
		);
		
	}
	
	public static ContiguousPixelPath findMaxSize( List<ContiguousPixelPath> vertices ) {
		ContiguousPixelPath max = null;
		int maxSize = Integer.MIN_VALUE;
		for( ContiguousPixelPath cpp : vertices ) {
			if (cpp.size() > maxSize) {
				maxSize = cpp.size();
				max = cpp;
			}
		}
		return max;
	}
	
	public void findAllCycles( ContiguousPixelPath s) {
		
		// Mark all the vertices as not visited(By default 
        // set as false) 
        Set<ContiguousPixelPath> visited = new HashSet<>(); 
  
        // Create a queue for BFS 
        LinkedList<ContiguousPixelPath> queue = new LinkedList<ContiguousPixelPath>(); 
  
        // Mark the current node as visited and enqueue it 
        visited.add(s); 
        queue.add(s); 
  
        while (!queue.isEmpty()) 
        { 
            // Dequeue a vertex from queue and print it 
            s = queue.poll(); 

  
            // Get all adjacent vertices of the dequeued vertex s 
            // If a adjacent has not been visited, then mark it 
            // visited and enqueue it 
            Iterator<ContiguousPixelPath> i = graph.adjacentVertices(s).iterator(); 
            while (i.hasNext()) 
            { 
            	ContiguousPixelPath n = i.next(); 
                if (!visited.contains(n)) 
                { 
                    visited.add(n); 
                    queue.add(n); 
                }  else {
                	System.out.printf("Cycle ends with %s->%s%n", s, n);
                	// Cycle found
                }
            } 
        } 
	}
}
