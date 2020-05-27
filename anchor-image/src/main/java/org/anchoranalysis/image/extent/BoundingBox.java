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
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.Tuple3i;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A bounding-box in 2 or 3 dimensions
 * 
 * A 2D bounding-box should always have a z-extent of 1 pixel
 *
 */
public final class BoundingBox implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Point3i crnrMin;
	private Extent extent;
	
	public BoundingBox() {
		this( new Extent() );
	}
	
	public BoundingBox(Extent extnt) {
		this( new Point3i(0,0,0), extnt);
	}

	public BoundingBox( Point3d min, Point3d max ) {
		this(
			PointConverter.intFromDouble(min),
			PointConverter.intFromDoubleCeil(max)
		);
	}

	public BoundingBox( Point3i min, Point3i max ) {
		this(
			min,
			new Extent(
				max.getX() - min.getX() + 1,
				max.getY() - min.getY() + 1,
				max.getZ() - min.getZ() + 1
			)
		);
		checkMaxMoreThanMin(min, max);
	}
	
	// Extnt is the number of pixels need to represent this bounding box
	public BoundingBox(Point3i crnrMin, Extent extent) {
		this.crnrMin = new Point3i(crnrMin);
		this.extent = extent;
	}
	
	
	/** 
	 * A mid-point in the bounding box, in the exact half way point between (crnr+extent)/2.
	 *  
	 * <p>It may not be integral, and could end with .5</p>
	 *  
	 * @return
	 */
	public Point3d midpoint() {
		return meanOfExtent(0);
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
			meanOfExtent(1)
		);
	}
		
	public BoundingBox flattenZ() {
		return new BoundingBox(
			crnrMin.duplicateChangeZ(0),
			extent.duplicateChangeZ(1)
		);
	}
	
	public BoundingBox duplicateChangeExtentZ(int extentZ) {
		return new BoundingBox(
			crnrMin,
			extent.duplicateChangeZ(extentZ)
		);
	}
	
	public BoundingBox duplicateChangeCornerZ(int crnrZ) {
		return new BoundingBox(
			crnrMin.duplicateChangeZ(crnrZ),
			extent
		);
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
	
	// An extent representing the number of pixels needed to represent the bounding box
	public Extent extent() {
		return this.extent;
	}
	
	// Copy constructor
	public BoundingBox( BoundingBox src ) {
		this.crnrMin = new Point3i( src.crnrMin );
		this.extent = src.extent;
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
	    if (!objCast.extent().equals(extent())) {
	    	return false;
	    }
	    
	    return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append( getCrnrMin() )
			.append( extent() )
			.toHashCode();
	}
	
	/** 
	 * 
	 * The bottom-left corner of the bounding box.
	 * 
	 * <p>The return value should be treated read-only, as this class is designed to be IMMUTABLE</o>.
	 * 
	 * */
	public Point3i getCrnrMin() {
		return crnrMin;
	}

	public void setExtent(Extent extnt) {
		this.extent = extnt;
	}
	
	public void growBy(Tuple3i toAdd, Extent containingExtent) {
		
		Point3i grow = new Point3i(toAdd);
		
		// Subtract the padding from the corner
		crnrMin.sub(grow);

		// Double the padding in each dimension, and add it to the extent
		grow.scale(2);
		extent = extent.growBy(grow);
		
		// Clip to make sure we remain within bounds
		clipTo(containingExtent);
	}
	
	// This is the last point INSIDE the box
	// So iterators should be <= CalcCrnrMax
	public Point3i calcCrnrMax() {
		Point3i p = new Point3i();
		p.setX(crnrMin.getX() + extent.getX() - 1);
		p.setY(crnrMin.getY() + extent.getY() - 1);
		p.setZ(crnrMin.getZ() + extent.getZ() - 1);
		return p;
	}
	
	public void clipTo( Extent e ) {
		
		Point3i crnrMax = calcCrnrMax();
		
		if (crnrMin.getX()<0) crnrMin.setX(0);
		if (crnrMin.getY()<0) crnrMin.setY(0);
		if (crnrMin.getZ()<0) crnrMin.setZ(0);
		
		if (crnrMax.getX()>=e.getX()) crnrMax.setX(e.getX() - 1);
		if (crnrMax.getY()>=e.getY()) crnrMax.setY(e.getY() - 1);
		if (crnrMax.getZ()>=e.getZ()) crnrMax.setZ(e.getZ() - 1);
		
		extent = new Extent(
			crnrMax.getX() - crnrMin.getX() + 1,
			crnrMax.getY() - crnrMin.getY() + 1,
			crnrMax.getZ() - crnrMin.getZ() + 1
		);
	}
	

	
	public Point3i closestPntOnBorder( Point3d pntIn ) {
		
		Point3i crnrMax = calcCrnrMax();
		
		Point3i pntOut = new Point3i();
		pntOut.setX( closestPntOnAxis(pntIn.getX(), crnrMin.getX(), crnrMax.getX()) );
		pntOut.setY( closestPntOnAxis(pntIn.getY(), crnrMin.getY(), crnrMax.getY()) );
		pntOut.setZ( closestPntOnAxis(pntIn.getZ(), crnrMin.getZ(), crnrMax.getZ()) );
		return pntOut;
	}
	
	public static Point3i relPosTo( Point3i relPoint, Point3i srcPoint ) {
		Point3i p = new Point3i( relPoint );
		p.sub( srcPoint );
		return p; 
	}
	
	// returns the relative position of the corner to another bounding box
	public Point3i relPosTo( BoundingBox src ) {
		return relPosTo( crnrMin, src.crnrMin );
	}
	
	/** 
	 * A new bounding-box using relative position coordinates to another box
	 * 
	 * @param other the other box, against whom we consider our co-ordinates relatively
	 * @return a newly created bounding box with relative coordinates
	 */
	public BoundingBox relPosToBox( BoundingBox other ) {
		return new BoundingBox(
			relPosTo(other),
			extent
		);
	}

	/** For evaluating whether this bounding-box contains other points, boxes etc.? */
	public BoundingBoxContains contains() {
		return new BoundingBoxContains(this); 
	}
	
	/** For evaluating the intersection between this bounding-box and others */
	public BoundingBoxIntersection intersection() {
		return new BoundingBoxIntersection(this);
	}
	
	/** For performing a union between this bounding-box and another */
	public BoundingBoxUnion union() {
		return new BoundingBoxUnion(this);
	}
	
	@Override
	public String toString() {
		return crnrMin.toString() + "+" + extent.toString() + "=" + calcCrnrMax().toString();
	}
	
	public void scaleXYPos( ScaleFactor sf ) {
		crnrMin.setX( ScaleFactorUtilities.multiplyAsInt(sf.getX(), crnrMin.getX()) );
		crnrMin.setY( ScaleFactorUtilities.multiplyAsInt(sf.getY(), crnrMin.getY()) );
	}
	
	public void scaleXYPosAndExtnt( ScaleFactor sf ) {
		scaleXYPos(sf);
		this.extent = extent.scaleXYBy(sf);
	}
	
	private void checkMaxMoreThanMin( Point3i min, Point3i max ) {
		if ((max.getX() < min.getX()) || (max.getY() < min.getY()) || (max.getZ() < min.getZ())) {
			throw new AnchorFriendlyRuntimeException(
				String.format("To create a bounding-box, the max-point %s must always be >= the min-point %s in all dimensions.", max, min)
			);
		}
	}
	
	private Point3d meanOfExtent( int subtractFromEachDimension ) {
		Point3d pnt = new Point3d();
		
		double e_x = ((double) this.extent.getX() - subtractFromEachDimension)/2;
		double e_y = ((double) this.extent.getY() - subtractFromEachDimension)/2;
		double e_z = ((double) this.extent.getZ() - subtractFromEachDimension)/2;
		
		pnt.setX( e_x + this.crnrMin.getX() ); 
		pnt.setY( e_y + this.crnrMin.getY() );
		pnt.setZ( e_z + this.crnrMin.getZ() );
		
		return pnt;
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
}
