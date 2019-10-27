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
import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.graph.GraphWithEdgeTypes;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.factory.CreateFromPointsFactory;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.voxel.nghb.CreateNghbGraph;

/**
 * A graph with all possible merges of ContiguousPixelPath
 * 
 * Each ContiguousPixelPath is a vertex
 * An edge with a cost, represents a possible merge between them
 * 
 * @author FEEHANO
 *
 */
public class MergePathGraphNghb {
	
	private GraphWithEdgeTypes<RasterizedPath,PotentialMerge> nghbs;
	
	private static class RasterizedPath {
		
		private int id;	// Unique useful for tracking
		private ContiguousPixelPath path;
		private ObjMask rasterized;
		
		public RasterizedPath(int id, ContiguousPixelPath path, ObjMask rasterized) {
			super();
			this.id = id;
			this.path = path;
			this.rasterized = rasterized;
		}		
		
		public int getId() {
			return id;
		}

		public ObjMask getRasterized() {
			return rasterized;
		}

		@Override
		public String toString() {
			return String.format(
				"Path(id=%3d,head=%s,tail=%s,length=%4d)",
				id,
				path.head(),
				path.tail(),
				path.size()
			);
		}
	}
	
	private static class PotentialMerge {
		
		private int id1;
		private int id2;
		private int numNghbPixels;
		
		public PotentialMerge(int id1, int id2, int numNghbPixels) {
			super();
			this.id1 = id1;
			this.id2 = id2;
			this.numNghbPixels = numNghbPixels;
		}

		@Override
		public String toString() {
			return String.format(
				"%d->%d(%d)",
				id1,
				id2,
				numNghbPixels
			);
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
	public MergePathGraphNghb( List<ContiguousPixelPath> vertices, Extent sceneExtnt ) throws CreateException {
		
		// Let's create a rasterized form of each path, and dilate by 1 (big-nghb)
		// This allows us to test for intersection
		List<RasterizedPath> rasterized = createRasterized(vertices);
		
		CreateNghbGraph<RasterizedPath> creator = new CreateNghbGraph<>(true);
		nghbs = creator.createGraph(
			rasterized,
			a->a.getRasterized(),
			(v1, v2, numPixels) -> new PotentialMerge( v1.getId(), v2.getId(), numPixels ),
			sceneExtnt,
			false,
			true,
			false,
			false
		);
		
		System.out.println(nghbs);
		
	}
	
	private static List<RasterizedPath> createRasterized( List<ContiguousPixelPath> paths ) {
		
		List<RasterizedPath> out = new ArrayList<RasterizedPath>();
		
		for( int i=0; i<paths.size(); i++ ) {
			ContiguousPixelPath path = paths.get(i);
			ObjMask rasterized = CreateFromPointsFactory.create( path.points() );
			
			out.add(
				new RasterizedPath(i, path, rasterized)	
			);
		}
		
		return out;
	}
}
