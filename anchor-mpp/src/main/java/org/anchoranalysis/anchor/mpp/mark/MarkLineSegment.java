package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;

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

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.points.BoundingBoxFromPoints;

public class MarkLineSegment extends Mark {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6436383113190855927L;
	// START mark state
	
	private static byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
	private static byte FLAG_SUBMARK_INSIDE = RegionMembershipUtilities.flagForRegion( GlobalRegionIdentifiers.SUBMARK_INSIDE );
	
	//private double distToLineForInside = 0.866025403;
	//private double distToLineForInside = 0.70710;
	private double distToLineForInside = 0.5;
	// END mark state
	
	private DistCalcToLine distCalcToLine = new DistCalcToLine();
	
	
	// This isn't very efficient for lines, as we can analytically determine
	//   which pixels are inside
	// We assume this is only ever called for points within the bounding box, otherwise
	//  we need to do a check and reject all others
	@Override
	public byte evalPntInside(Point3d pt) {
		
		// TODO
		// This should be half the distance from one corner of a pixel/voxel to another
		// And it thus depends on the number of dimensions
		// In future we calculate this in a better way
		
		// Half the square root of 3
		//double thresh = 0.866025403;
		//double thresh = 0.70710;
		//double thresh = 0.5;
		//DistCalcToLine distCalcToLine = new DistCalcToLine();
		
		if (distCalcToLine.distToLine(pt)<distToLineForInside) {
			return FLAG_SUBMARK_INSIDE;
		}
		
		return FLAG_SUBMARK_NONE;
	}
	
	
	@Override
	public BoundingBox bbox(ImageDim bndScene, int regionID) {
		return BoundingBoxFromPoints.forTwoPoints( distCalcToLine.getStartPoint(), distCalcToLine.getEndPoint() );
	}

	@Override
	public Mark duplicate() {
		MarkLineSegment out = new MarkLineSegment();
		out.setPoints( distCalcToLine.getStartPoint(), distCalcToLine.getEndPoint() );
		return out;
	}

	@Override
	public double volume( int regionID ) {
		// The Line Length
		return distCalcToLine.getStartPoint().distance( distCalcToLine.getEndPoint() );
	}

	@Override
	public String toString() {
		return String.format("%s-%s", distCalcToLine.getStartPoint().toString(), distCalcToLine.getEndPoint().toString() );
	}

	@Override
	public int numDims() {
		return 3;
	}

	@Override
	public String getName() {
		return "markLineSegment";
	}

	@Override
	public void scale(double mult_factor) {
		MarkAbstractPosition.scaleXYPnt( distCalcToLine.getStartPoint(), mult_factor);
		MarkAbstractPosition.scaleXYPnt( distCalcToLine.getEndPoint(), mult_factor);
		
	}

	@Override
	public Point3d centerPoint() {
		Point3d pnt = new Point3d( distCalcToLine.getStartPoint() );
		pnt.add( distCalcToLine.getEndPoint() );
		pnt.scale( 0.5 );
		return pnt;
	}

	public void setPoints( Point3d startPoint, Point3d endPoint ) {
		distCalcToLine.setPoints(startPoint, endPoint);
		clearCacheID();
	}

	public Point3d getStartPoint() {
		return distCalcToLine.getStartPoint();
	}


	public Point3d getEndPoint() {
		return distCalcToLine.getEndPoint();
	}

	public Point3d getDirectionVector() {
		return distCalcToLine.getDirectionVector();
	}

	@Override
	public int numRegions() {
		return 1;
	}
	
	@Override
	public BoundingBox bboxAllRegions(ImageDim bndScene) {
		return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_INSIDE);
	}
}
