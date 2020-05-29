package org.anchoranalysis.anchor.mpp.mark.points;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;

public class MarkPointListFactory {

	public static MarkPointList create( List<Point3d> pnts ) {
		return create(pnts, -1);
	}
	
	public static MarkPointList create( List<Point3d> pnts, int id ) {
		MarkPointList mark = new MarkPointList();
		mark.getPoints().addAll( pnts );
		mark.setId(id);
		mark.updateAfterPointsChange();
		return mark;
	}
		
	public static MarkPointList createMarkFromPoints3f( List<Point3f> pts, boolean round ) {
		MarkPointList markPts = new MarkPointList();
		markPts.getPoints().addAll(
			PointConverter.convert3f_3d(pts)
		);
		markPts.updateAfterPointsChange();
		return markPts;
	}
	
	public static MarkPointList createMarkFromPoints3i( List<Point3i> pts ) {
		assert( pts.size()>= 1 );
		MarkPointList markPts = new MarkPointList();
		
		for (Point3i p : pts) {
			markPts.getPoints().add( PointConverter.doubleFromInt(p) );
		}
		markPts.updateAfterPointsChange();
		return markPts;
	}
}
