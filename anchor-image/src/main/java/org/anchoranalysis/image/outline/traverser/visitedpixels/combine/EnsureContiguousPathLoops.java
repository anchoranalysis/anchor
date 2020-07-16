package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;

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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.contiguouspath.PointsListNeighborUtilities;

/** Makes sure the head() and tail() of a ContiguousPath are neighbours */
class EnsureContiguousPathLoops {
	
	private EnsureContiguousPathLoops() {}
	
	/** Ensures the head() and tail() of a path are neighbours, chopping off points if appropriate */
	public static boolean apply(ContiguousPixelPath path) {
		
		// Look for all points on the path which neighbour each other
		// Consider all possibilities, and implement the cut that removes the minimal pixels
		IndexOffsets offsets = new FindMinimum(path).apply();
		
		path.removeLeft( offsets.left() );
		path.removeRight( offsets.rightReversed() );
		
		return true;
	}
	
	/**
	 * A left and right index
	 * 
	 * @author feehano
	 *
	 */
	private static class IndexOffsets {

		private int left = -1;
		private int right = -1;
		private int size;
		
		public IndexOffsets(int size) {
			this.size = size;
		}

		public IndexOffsets(int size, int left, int right) {
			super();
			this.size = size;
			this.left = left;
			this.right = right;
		}
		
		/**
		 * Explictly updates the left and right index
		 * 
		 * @param left
		 * @param right
		 */
		public void update(int left, int right) {
			this.left = left;
			this.right = right;
		}
		
		/**
		 * Updates left and right indexes to be a particular distance from both ends of the path
		 * @param dist
		 */
		public void updateDistFromSides( int dist ) {
			update(
				Math.min( size, dist ),
				Math.max( size - dist, 0 )
			);
		}

		public int left() {
			return left;
		}

		public int right() {
			return right;
		}
		
		/** The index relative to the right-side of the paht */
		public int rightReversed() {
			return size - right - 1;
		}
	}
	
	private static class FindMinimum {
		
		private int minCost = Integer.MAX_VALUE;
		private IndexOffsets minCostIndexes;
		private IndexOffsets boundIndexes;
		private ContiguousPixelPath path;
		
		public FindMinimum( ContiguousPixelPath path ) {
			
			this.path = path;
			
			minCostIndexes = new IndexOffsets(path.size());
			
			// PLACES BOUNDS ON WHERE WE SEARCH
			// We will never search beyond this index, as a cost will
			//  always be greater than one we already found
			boundIndexes = new IndexOffsets(path.size(), path.size(), 0);
		}
		
		public IndexOffsets apply() {
						
			for( int i=0; i<boundIndexes.left(); i++) {
				Point3i pointLeft = path.get(i);
				
				for( int j=boundIndexes.right(); j<path.size(); j++) {
					
					if (i==j) {
						continue;
					}

					maybeUpdate(i, j, pointLeft, path.get(j) );
				}
			}
			
			if (minCostIndexes.left()==-1) {
				// We failed to find any neighbouring points to cut with
				// This should never happen, as long as there are at least two neighbouring points
				throw new AnchorImpossibleSituationException();
			}
			
			return minCostIndexes;
		}
		
		private void maybeUpdate( int i, int j, Point3i pointLeft, Point3i pointRight ) {

			if (PointsListNeighborUtilities.arePointsNghb(pointLeft, pointRight)) {
				
				// Then it's possible we make these two points the head and tail of 
				//   the list. This would cost us a certain number of pixels, that
				//  we would need to remove.
				int cost = i + (path.size()-j-1);
				
				if (cost<minCost) {
					minCostIndexes.update(i, j);
					minCost = cost;
					boundIndexes.updateDistFromSides(cost);
				}
				
			}
		}
		
	}
	
}
