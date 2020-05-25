package org.anchoranalysis.image.extent;

/*
 * #%L
 * anchor-image
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


import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A bounding-box in 2 or 3 dimensions
 * 
 * A 2D bounding-box should always have a z-extent of 1 pixel
 *
 */
public class BoundingBox implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Point3i crnrMin;
	private Extent extnt;
	
	public BoundingBox() {
		this( new Extent() );
	}
	
	public BoundingBox(Extent extnt) {
		this( new Point3i(0,0,0), new Extent(extnt) );
	}
	
	
	// Extnt is the number of pixels need to represent this bounding box
	public BoundingBox(Point3i crnrMin, Extent extnt) {
		super();
		this.crnrMin = new Point3i(crnrMin);
		this.extnt = new Extent(extnt);
	}
	
	public BoundingBox( Point3d min, Point3d max ) {
		this(
			new Point3i( (int) Math.floor(min.getX()), (int) Math.floor(min.getY()), (int) Math.floor(min.getZ()) ),
			new Point3i( (int) Math.ceil(max.getX()), (int) Math.ceil(max.getY()), (int) Math.ceil(max.getZ()) )
		);
	}
	
	public BoundingBox( Point3i min, Point3i max ) {
		this.crnrMin = new Point3i(min);
		
		checkMaxMoreThanMin(min, max);
		
		this.extnt = new Extent(
			max.getX() - min.getX() + 1,
			max.getY() - min.getY() + 1,
			max.getZ() - min.getZ() + 1
		);
	}
	
	/** 
	 * A mid-point in the bounding box, in the exact half way point between (crnr+extent)/2.
	 *  
	 * <p>It may not be integral, and could end with .5</p>
	 *  
	 * @return
	 */
	public Point3d midpoint() {
		return meanOfPoints(0);
	}

	/** 
	 * Similar to {@link midpoint} but not always identical. It is the mean of all the points in the box, and guaranteed to be integral.
	 * 
	 * <p>It should be the same in each dimension as (crnr+extent-1)/2</p>
	 *  
	 * @return
	 */

	public Point3i centerOfGravity() {
		return new Point3i(
			meanOfPoints(1)
		);
	}
	
	private Point3d meanOfPoints( int subtractFromEachDimension ) {
		Point3d pnt = new Point3d();
		
		double e_x = ((double) this.extnt.getX() - subtractFromEachDimension)/2;
		double e_y = ((double) this.extnt.getY() - subtractFromEachDimension)/2;
		double e_z = ((double) this.extnt.getZ() - subtractFromEachDimension)/2;
		
		pnt.setX( e_x + this.crnrMin.getX() ); 
		pnt.setY( e_y + this.crnrMin.getY() );
		pnt.setZ( e_z + this.crnrMin.getZ() );
		
		return pnt;
	}
	
	public void convertToMaxIntensityProj() {
		this.crnrMin.setZ(0);
		this.extnt.setZ( 1 );
	}
	
	public boolean atBorder( ImageDim sd ) {

		if (atBorderXY(sd)) return true;
		
		if (atBorderZ(sd)) return true;
		
		return false;
	}
	
	public boolean atBorderXY( ImageDim sd ) {
		
		Point3i crnrMax = this.calcCrnrMax();

		if (crnrMin.getX()==0) {
			return true;
		}
		if (crnrMin.getY()==0) {
			return true;
		}
		
		if (crnrMax.getX()==(sd.getX()-1)) {
			return true;
		}
		if (crnrMax.getY()==(sd.getY()-1)) {
			return true;
		}
		
		return false;
	}
	
	public boolean atBorderZ( ImageDim sd ) {
		
		Point3i crnrMax = this.calcCrnrMax();
		
		if (crnrMin.getZ()==0) return true;
		if (crnrMax.getZ()==(sd.getZ()-1)) return true;
		
		return false;
	}
	
	public void add( Point3i pnt ) {
		add( pnt.getX(), pnt.getY(), pnt.getZ() );
	}
	
	
	public void add( int x, int y, int z ) {

		Point3i crnrMax = calcCrnrMax();
		
		if (x < crnrMin.getX()) { crnrMin.setX(x); extnt.setX( crnrMax.getX() - x + 1); };
		if (y < crnrMin.getY()) { crnrMin.setY(y); extnt.setY( crnrMax.getY() - y + 1); };
		if (z < crnrMin.getZ()) { crnrMin.setZ(z); extnt.setZ( crnrMax.getZ() - z + 1); };
		
		if (x > crnrMax.getX()) { extnt.setX( x - crnrMin.getX() + 1 ); }
		if (y > crnrMax.getY()) { extnt.setY( y - crnrMin.getY() + 1 ); }
		if (z > crnrMax.getZ()) { extnt.setZ( z - crnrMin.getZ() + 1 ); }
	}
	
	// An extent representing the number of pixels needed to represent the bounding box
	public Extent extnt() {
		return this.extnt;
	}
	
	// Copy constructor
	public BoundingBox( BoundingBox src ) {
		this.crnrMin = new Point3i( src.crnrMin );
		this.extnt = new Extent( src.extnt );
	}
	

	@Override
	public boolean equals( Object obj ) {
		if (this == obj) return true;
	    if (!(obj instanceof BoundingBox)) {
	        return false;
	    }
	    
	    BoundingBox objCast = (BoundingBox) obj;
	    if (!objCast.getCrnrMin().equals(getCrnrMin())) {
	    	return false;
	    }
	    if (!objCast.extnt().equals(extnt())) {
	    	return false;
	    }
	    
	    return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append( getCrnrMin() )
			.append( extnt() )
			.toHashCode();
	}
	
	public Point3i getCrnrMin() {
		return crnrMin;
	}
	public void setCrnrMin(Point3i crnrMin) {
		this.crnrMin = crnrMin;
	}

	public void setExtnt(Extent extnt) {
		this.extnt = extnt;
	}
	
	public int getCrnrMinForAxis(AxisType axisType) {
		switch(axisType) {
		case X: {
			return crnrMin.getX();
		}
		case Y: {
			return crnrMin.getY();
		}
		case Z: {
			return crnrMin.getZ();
		}
		default:
			assert false;
			return 0;
		}
	}
	
	// This is the last point INSIDE the box
	// So iterators should be <= CalcCrnrMax
	public Point3i calcCrnrMax() {
		Point3i p = new Point3i();
		p.setX(crnrMin.getX() + extnt.getX() - 1);
		p.setY(crnrMin.getY() + extnt.getY() - 1);
		p.setZ(crnrMin.getZ() + extnt.getZ() - 1);
		return p;
	}
	
	// Returns the shift that occurs from the x-side
	public Point3i clipTo( Extent e ) {
		
		int xOld = crnrMin.getX();
		int yOld = crnrMin.getY();
		int zOld = crnrMin.getZ();
		
		Point3i crnrMax = calcCrnrMax();
		
		if (crnrMin.getX()<0) crnrMin.setX(0);
		if (crnrMin.getY()<0) crnrMin.setY(0);
		if (crnrMin.getZ()<0) crnrMin.setZ(0);
		
		if (crnrMax.getX()>=e.getX()) crnrMax.setX(e.getX() - 1);
		if (crnrMax.getY()>=e.getY()) crnrMax.setY(e.getY() - 1);
		if (crnrMax.getZ()>=e.getZ()) crnrMax.setZ(e.getZ() - 1);
		
		extnt = new Extent(
			crnrMax.getX() - crnrMin.getX() + 1,
			crnrMax.getY() - crnrMin.getY() + 1,
			crnrMax.getZ() - crnrMin.getZ() + 1
		);
		
		return new Point3i( crnrMin.getX()-xOld, crnrMin.getY()-yOld, crnrMin.getZ()-zOld );
	}
	
	public boolean containsX( int x ) {
		return (x>= crnrMin.getX()) && (x< (crnrMin.getX() + extnt.getX()) ); 
	}
	
	public boolean containsY( int y ) {
		return (y>= crnrMin.getY()) && (y< (crnrMin.getY() + extnt.getY()) ); 
	}
	
	public boolean containsZ( int z ) {
		return (z>= crnrMin.getZ()) && (z< (crnrMin.getZ() + extnt.getZ()) ); 
	}
	
	public boolean containsIgnoreZ( Point3i pnt )  {
		return containsX( pnt.getX() ) && containsY( pnt.getY() );
	}
	
	public boolean contains( Point3i pnt ) {
		return containsX( pnt.getX() ) && containsY( pnt.getY() ) && containsZ( pnt.getZ() );
	}
	
	public boolean contains( BoundingBox box ) {
		return contains( box.getCrnrMin() ) && contains( box.calcCrnrMax() );
	}
	
	
	private static int closestPntOnAxis( double val, int axisMin, int axisMax) {
		
		if (val<axisMin) {
			return axisMin;
		}
		
		if (val>axisMax) {
			return axisMax;
		}
		
		return (int) val;
	}
	
	public Point3i closestPntOnBorder( Point3d pntIn ) {
		
		Point3i crnrMax = calcCrnrMax();
		
		Point3i pntOut = new Point3i();
		pntOut.setX( closestPntOnAxis(pntIn.getX(), crnrMin.getX(), crnrMax.getX()) );
		pntOut.setY( closestPntOnAxis(pntIn.getY(), crnrMin.getY(), crnrMax.getY()) );
		pntOut.setZ( closestPntOnAxis(pntIn.getZ(), crnrMin.getZ(), crnrMax.getZ()) );
		return pntOut;
	}

	
	public void flattenZ() {
		this.crnrMin.setZ(0);
		this.extnt.setZ(1);
	}
	
	public static Point3i relPosTo( Point3i relPoint, Point3i srcPoint ) {
		Point3i p = new Point3i( relPoint );
		p.sub( srcPoint );
		return p; 
	}
	
	// returns the relative position to another bounding box
	public Point3i relPosTo( BoundingBox src ) {
		return relPosTo( crnrMin, src.crnrMin );
	}
	
	public BoundingBox intersectCreateNewNoClip( BoundingBox bbox ) {
		BoundingBox bboxIntersect = new BoundingBox( this );
		if (!bboxIntersect.intersect(bbox,true)) {
			return null;
		}
		return bboxIntersect;
	}
	
	public BoundingBox intersectCreateNew( BoundingBox bbox, Extent e ) {
		BoundingBox bboxIntersect = new BoundingBox( this );
		if (!bboxIntersect.intersect(bbox,true)) {
			return null;
		}
		bboxIntersect.clipTo( e );
		return bboxIntersect;
	}
	
	// Does not modify state
	public boolean intersect( List<BoundingBox> othrList ) {
		
		for (BoundingBox othr : othrList) {
			
			if (hasIntersection(othr)) {
				return true;
			}
		}
		
		return false;
	}
	
	// Does not modify state
	public boolean hasIntersection( BoundingBox othr ) {
		return intersect(othr, false);
	}
	
	
	public boolean intersect( BoundingBox othr, boolean modifyState ) {
		
		Point3i crnrMax = calcCrnrMax();
		Point3i crnrMaxOthr = othr.calcCrnrMax();
		
		Optional<ExtentIntersector> meiX = ExtentIntersector.createMin(crnrMin, othr.crnrMin, crnrMax, crnrMaxOthr, p->p.getX() );
		Optional<ExtentIntersector> meiY = ExtentIntersector.createMin(crnrMin, othr.crnrMin, crnrMax, crnrMaxOthr, p->p.getY() );
		Optional<ExtentIntersector> meiZ = ExtentIntersector.createMin(crnrMin, othr.crnrMin, crnrMax, crnrMaxOthr, p->p.getZ() );
		
		if (!meiX.isPresent() || !meiY.isPresent() || !meiZ.isPresent()) {
			return false;
		}
				
		if (modifyState) {
			crnrMin = new Point3i(
				meiX.get().getMin(),
				meiY.get().getMin(),
				meiZ.get().getMin()
			);
			
			extnt = new Extent(
				meiX.get().getExtnt(),
				meiY.get().getExtnt(),
				meiZ.get().getExtnt()
			);
		}
		
		return true;
	}
	
	public static BoundingBox intersect( BoundingBox bbox1, BoundingBox bbox2 ) {
		BoundingBox b = new BoundingBox(bbox1);
		b.intersect(bbox2,true);
		return b;
	}
	
	public static BoundingBox union( BoundingBox bbox1, BoundingBox bbox2 ) {
		BoundingBox b = new BoundingBox(bbox1);
		b.union(bbox2);
		return b;
	}
	
	public void union( BoundingBox othr ) {
		
		Point3i crnrMax = calcCrnrMax();
		Point3i crnrMaxOthr = othr.calcCrnrMax();
		
		ExtentIntersector meiX = ExtentIntersector.createMax(crnrMin, othr.crnrMin, crnrMax, crnrMaxOthr, p->p.getX() );
		ExtentIntersector meiY = ExtentIntersector.createMax(crnrMin, othr.crnrMin, crnrMax, crnrMaxOthr, p->p.getY() );
		ExtentIntersector meiZ = ExtentIntersector.createMax(crnrMin, othr.crnrMin, crnrMax, crnrMaxOthr, p->p.getZ() );

		crnrMin = new Point3i(
			meiX.getMin(),
			meiY.getMin(),
			meiZ.getMin()
		);
		
		extnt = new Extent(
			meiX.getExtnt(),
			meiY.getExtnt(),
			meiZ.getExtnt()
		);
	}
	
	public void shrinkByQuantiles( double quantileLower, double quantileHigher ) {
		
		// Shrink each bbox to a quantile
		int zLow = (int) Math.floor( quantileLower * extnt().getZ() );
		int zHigh = (int) Math.ceil( quantileHigher * extnt().getZ() );
		
		int zSize = zHigh - zLow + 1;
		
		crnrMin.setZ( crnrMin.getZ() + zLow );
		extnt.setZ( zSize );
	}

	public Point3d calcRelToLowerEdge( Point3d pntIn ) {
		
		Point3d pntOut = new Point3d(pntIn);
		pntOut.sub( new Point3d(this.crnrMin.getX(), this.crnrMin.getY(), this.crnrMin.getZ() ) );
		return pntOut;
	}
	
	public Point3i calcRelToLowerEdgeInt( Point3d pntIn ) {
		
		Point3d relPntDbl = calcRelToLowerEdge(pntIn);
		
		Point3i relPntInt = new Point3i();
		relPntInt.setX((int) relPntDbl.getX() );
		relPntInt.setY((int) relPntDbl.getY() );
		relPntInt.setZ((int) relPntDbl.getZ() );
		return relPntInt;
	}

	@Override
	public String toString() {
		return crnrMin.toString() + "+" + extnt.toString() + "=" + calcCrnrMax().toString();
	}
	
	public void scaleXYPos( ScaleFactor sf ) {
		crnrMin.setX( ScaleFactorUtilities.multiplyAsInt(sf.getX(), crnrMin.getX()) );
		crnrMin.setY( ScaleFactorUtilities.multiplyAsInt(sf.getY(), crnrMin.getY()) );
	}
	
	public void scaleXYPosAndExtnt( ScaleFactor sf ) {
		scaleXYPos(sf);
		extnt.scaleXYBy(sf);
	}
	
	private void checkMaxMoreThanMin( Point3i min, Point3i max ) {
		if ((max.getX() < min.getX()) || (max.getY() < min.getY()) || (max.getZ() < min.getZ())) {
			throw new AnchorFriendlyRuntimeException(
				String.format("To create a bounding-box, the max-point %s must always be >= the min-point %s in all dimensions.", max, min)
			);
		}
	}
}
