package org.anchoranalysis.image.voxel.nghb;

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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.index.ObjectCollectionRTree;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.morph.MorphologicalDilation;

/**
 * Adds edges if objects neighbour each other
 * 
 * @author Owen Feehan
 *
 * @param <V> vertice-type
 */
class EdgeAdder<V> {
	
	private List<V> verticesAsList;
	private Function<V,ObjectMask> vertexToObjMask;
	private ObjectCollectionRTree rTree;
	private AddEdge<V> addEdge;
	private boolean preventObjIntersection;
	private boolean bigNghb;
	private boolean testBothDirs;

	@FunctionalInterface
	public static interface AddEdge<V> {
		void addEdge( V src, V dest, int numBorderPixels );
	}
	
	/**
	 * Adds edges by checking if a Vertex intersects with another Vertex
	 * 
	 * This is done always by finding an ObjMask representation of each vertex
	 * 
	 * @param verticesAsList a list of vertices
	 * @param vertexToObjMask how to convert a individual vertice to an object mask
	 * @param rTree the rTree underpinning the vertices (or rather their derived object-masks)
	 * @param graph
	 * @param preventObjIntersection avoids any edge if any two objects have a common pixel
	 * @param undirected iff FALSE edges are considered in both directions independently
	 */
	public EdgeAdder(
		List<V> verticesAsList,
		Function<V,ObjectMask> vertexToObjMask,
		ObjectCollectionRTree rTree,
		AddEdge<V> addEdge,
		boolean preventObjIntersection,
		boolean bigNghb,
		boolean testBothDirs
	) {
		super();
		this.preventObjIntersection = preventObjIntersection;
		this.verticesAsList = verticesAsList;
		this.vertexToObjMask = vertexToObjMask;
		this.rTree = rTree;
		this.addEdge = addEdge;
		this.bigNghb = bigNghb;
		this.testBothDirs = testBothDirs;
	}
	
	public void addEdgesFor(
		int ignoreIndex,
		ObjectMask om,
		V vertexWith,
		Extent sceneExtent,
		boolean do3D
	) throws CreateException {
		
		ObjectMask omDilated = MorphologicalDilation.createDilatedObjMask(
			om,
			Optional.of(sceneExtent),
			do3D && sceneExtent.getZ()>1,
			1,
			bigNghb
		);
		
		addWithDilatedMask( ignoreIndex, om, vertexWith, omDilated );
	}
	
	private void addWithDilatedMask(
		int ignoreIndex,
		ObjectMask om,
		V vertexWith,
		ObjectMask omDilated
	) {
		List<Integer> indicesIntersects = rTree.intersectsWithAsIndices( omDilated.getBoundingBox() );
		for( int j : indicesIntersects) {
			
			// We enforce an ordering, so as not to do the same pair twice (or the identity case)
			if (doSkipIndex(j, ignoreIndex)) {
				continue;			
			}
			
			V vertexOther = verticesAsList.get(j);
			
			maybeAddEdge(
				om,
				omDilated,
				vertexToObjMask.apply(vertexOther),
				vertexWith,
				verticesAsList.get(j)
			);
		}		
	}
	
	private boolean doSkipIndex(int index, int ignoreIndex) {
		if (testBothDirs) {
			if (index==ignoreIndex) {
				return true;
			}
		} else {
			if (index>=ignoreIndex) {
				return true;
			}				
		}
		return false;
	}
	
	private void maybeAddEdge(
		ObjectMask om,
		ObjectMask omDilated,
		ObjectMask omOther,
		V vertexWith,
		V vertexOther
	) {
		// Check that they don't overlap
		if (preventObjIntersection && om.hasIntersectingPixels(omOther)) {
			return;
		}
			
		// How many border pixels shared between the two?
		int numBorderPixels = numBorderPixels(omDilated, omOther); 
		if( numBorderPixels>0 ) {
			addEdge.addEdge(vertexWith,vertexOther,numBorderPixels);
		}
	}
	
	private static int numBorderPixels( ObjectMask om1Dilated, ObjectMask om2 ) {
		return om1Dilated.countIntersectingPixels(om2);
	}
}
