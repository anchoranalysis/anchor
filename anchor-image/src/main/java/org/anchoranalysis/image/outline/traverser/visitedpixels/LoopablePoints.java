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

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.geometry.Point3i;

/** A set of points that can be looped, so as to form a path that ends with
 *   the same point that it begins with.... without breaking continuity.
 * @author feehano
 *
 */
public class LoopablePoints {

	// This is a point that previously connected with the loop, but which we repeat
	//   after we do a loop, so that the end point is identical to the end of the previous
	//   points
	private Point3i connectingPnt;
	private List<Point3i> pnts;
	
	public LoopablePoints(List<Point3i> pnts, Point3i connectingPnt) {
		super();
		this.pnts = pnts;
		this.connectingPnt = connectingPnt;
	}
		
	public List<Point3i> loopPointsLeft() {
		List<Point3i> out = new ArrayList<>();
		
		out.add(connectingPnt);
		
		if (pnts.size()==1) {
			// Special behaviour for a single-point as it doesn't need reversing
			out.addAll(pnts);
		} else {
			out.addAll( ReverseUtilities.reversedList(pnts) );
			out.remove( out.size() - 1 );	// Remove last item to prevent duplication
			out.addAll(pnts);
		}
		
		return out;
	}
	
	public List<Point3i> loopPointsRight() {
		List<Point3i> out = new ArrayList<>( pnts );
		
		if (pnts.size()==1) {
			// Special behaviour for a single-point as it doesn't need reversing
		} else {
			out.remove( out.size() - 1 );	// Remove last item to prevent duplication
			out.addAll( ReverseUtilities.reversedList(pnts) );
		}
				
		out.add(connectingPnt);
		return out;
	}

	public int size() {
		return pnts.size();
	}

	public List<Point3i> points() {
		return pnts;
	}

	@Override
	public String toString() {
		return pnts.toString();
	}
}
