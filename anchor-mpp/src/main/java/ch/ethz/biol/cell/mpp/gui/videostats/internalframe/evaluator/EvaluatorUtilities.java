package ch.ethz.biol.cell.mpp.gui.videostats.internalframe.evaluator;

/*
 * #%L
 * anchor-mpp
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


import java.awt.Color;
import java.util.List;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkPointList;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkEllipse;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkEllipsoid;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.contour.Contour;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.image.orientation.Orientation3DEulerAngles;

public class EvaluatorUtilities {

	public static MarkPointList createMarkForContour(Contour c, boolean round ) {
		return EvaluatorUtilities.createMarkFromPoints3f( c.getPoints(), round);
	}

	public static ColorList createDefaultColorList() {
		ColorList colorList = new ColorList();
		colorList.add( new RGBColor(Color.BLUE) );		//  0 is the mark added
		colorList.add( new RGBColor(Color.RED) );		//  1 is any debug marks
		colorList.add( new RGBColor(Color.GREEN) );		//  2 centre point
		colorList.add( new RGBColor(Color.YELLOW) );
		return colorList;
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
	
	public static Mark createMarkFromPoint3d( Point3d pnt, int size, boolean do3D ) {
		assert(size>0);
		if (do3D) {
			MarkEllipsoid me = new MarkEllipsoid();
			me.setMarksExplicit(pnt, new Orientation3DEulerAngles(), new Point3d(size,size,size) );
			return me;
		} else {
			assert( pnt.getZ()==0 );
			MarkEllipse me = new MarkEllipse();
			me.setMarksExplicit(pnt, new Orientation2D(), new Point2d(size,size) );
			return me;
		}
	}

}
