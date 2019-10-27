package org.anchoranalysis.image.outline.traverser.visitedpixels;

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

class InsertLooped {

	/**
	 * Inserts points into the path, but doing looping (mirroring) if necessary to esnure connectivity
	 * 
	 * @param path
	 * @param loop
	 * @param mergePnt
	 * @param before if TRUE, it is inserted before the path. if FALSE, it is inserted afterwards.
	 */
	/*public static void insertLooped( List<Point3i> path, LoopablePoints pnts, boolean before ) {

		ArrayList<Point3i> pathBefore = new ArrayList<>( path );
		//System.out.printf("Merge pnt = %s%n", mergePnt);
		if (pnts!=null && pnts.size()>0) {
			if (before) {
				path.addAll( 0, pnts.loopPointsLeft() );
				
				// DEBUG
				assert( PointsListNghbUtilities.areAllPointsInBigNghb(path) );
				
			} else {
				System.out.printf("DEBUG insert after after=%s  loop=%s%n", path.get(path.size()-1), pnts.loopPointsRight() );
				path.addAll( pnts.loopPointsRight() );
				
				// DEBUG
				assert( PointsListNghbUtilities.areAllPointsInBigNghb(path) );
			}
		}
	}*/
	
}
