package org.anchoranalysis.anchor.mpp.mark;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.apache.commons.collections.ListUtils;

public abstract class MarkAbstractPointList extends Mark {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6520431317406007141L;

	// START BEAN PROPERTIES
	private List<Point3d> points;
	// END BEAN PROPERTIES

	private Point3d min;		// Contains the minimum x, y of all the points in the polygon
	private Point3d max;		// Contains the maximum x, y of all the points in the polygon
	
	public MarkAbstractPointList() {
		points = new ArrayList<>();
	}
	
	public List<Point3d> getPoints() {
		return points;
	}
	
	protected void doDuplicate(MarkAbstractPointList markNew) {
		markNew.points = new ArrayList<>();
		for (Point3d p : getPoints() ) {
			markNew.points.add(p);
		}
		markNew.setId( getId() );
		markNew.updateAfterPointsChange();		
	}
	
	
	public void updateAfterPointsChange() {
		
		assert(points.size()>=1);
				
		this.min = calcMin( getPoints() );
		this.max = calcMax( getPoints() );
	}

	@Override
	public BoundingBox bbox(ImageDim bndScene, int regionID) {
		// FOR NOW WE IGNORE THE SHELL RADIUS
		return new BoundingBox(min, max);
	}
	
	
	private static Point3d calcMin( List<Point3d> points ) {
		Point3d min = new Point3d(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
		for (Point3d p : points) {
			if (p.getX() < min.getX()) {
				min.setX( p.getX() );
			}
			
			if (p.getY() < min.getY()) {
				min.setY( p.getY() );
			}
			
			if (p.getZ() < min.getZ()) {
				min.setZ( p.getZ() );
			}
		}
		return min;
	}
	
	private static Point3d calcMax( List<Point3d> points ) {
		Point3d max = new Point3d(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
		for (Point3d p : points) {
			if (p.getX() > max.getX()) {
				max.setX( p.getX() );
			}
			
			if (p.getY() > max.getY()) {
				max.setY( p.getY() );
			}
			
			if (p.getZ() > max.getZ()) {
				max.setZ( p.getZ() );
			}
		}
		return max;
	}

	protected Point3d getMin() {
		return min;
	}

	protected Point3d getMax() {
		return max;
	}
	
	protected BoundingBox bbox() {
		return new BoundingBox( getMin(), getMax() );
	}

	@Override
	public boolean equalsDeep(Mark m) {
		
		if(m==null) { return false; }
		if (m==this) { return true; }
		
		if (!super.equalsDeep(m)) {
			return false;
		}
		
		if(m instanceof MarkAbstractPointList) {
			MarkAbstractPointList objCast = (MarkAbstractPointList) m;
			
			if (min!=objCast.getMin()) {
				return false;
			}
			
			if (max!=objCast.getMax()) {
				return false;
			}
			
			return ListUtils.isEqualList( points, objCast.getPoints() );
		} else {
			return false;
		}
	}

	@Override
	public void assignFrom(Mark srcMark) throws OptionalOperationUnsupportedException {
		super.assignFrom(srcMark);
		
		if (!(srcMark instanceof MarkAbstractPointList)) {
			throw new OptionalOperationUnsupportedException("srcMark must be of type MarkEllipse");
		}
		
		MarkAbstractPointList srcMarkCast = (MarkAbstractPointList) srcMark;
		points.clear();
		points.addAll( srcMarkCast.getPoints() );
		updateAfterPointsChange();	
	}

	
	

}
