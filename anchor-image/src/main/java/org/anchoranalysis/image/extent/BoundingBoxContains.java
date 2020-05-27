package org.anchoranalysis.image.extent;

import org.anchoranalysis.core.geometry.Point3i;

/**
 * Does a bounding box contain other objects? e.g. points, other bounding boxes etc.
 * 
 * @author Owen Feehan
 *
 */
public final class BoundingBoxContains {

	private final BoundingBox bbox;
	private final Point3i crnrMax;
	
	public BoundingBoxContains(BoundingBox bbox) {
		super();
		this.bbox = bbox;
		this.crnrMax = bbox.calcCrnrMax();
	}
	
	/** Is this value in the x-dimension within the bounding box range? */
	public boolean x( int x ) {
		return (x>= bbox.getCrnrMin().getX()) && (x<=crnrMax.getX()); 
	}

	/** Is this value in the y-dimension within the bounding box range? */
	public boolean y( int y ) {
		return (y>= bbox.getCrnrMin().getY()) && (y<crnrMax.getY()); 
	}
	
	/** Is this value in the z-dimension within the bounding box range? */
	public boolean z( int z ) {
		return (z>= bbox.getCrnrMin().getZ()) && (z<crnrMax.getZ()); 
	}
	
	/** Is this point within the bounding-box? */
	public boolean point( Point3i pnt ) {
		return x( pnt.getX() ) && y( pnt.getY() ) && z( pnt.getZ() );
	}

	/** Is this point within the bounding-box, but ignoring the z-dimension? */
	public boolean pointIgnoreZ( Point3i pnt )  {
		return x( pnt.getX() ) && y( pnt.getY() );
	}
	
	/** Is this other bounding-box FULLY contained within this bounding box? */ 
	public boolean box( BoundingBox maybeContainedInside ) {
		return point( maybeContainedInside.getCrnrMin() ) && point( maybeContainedInside.calcCrnrMax() );
	}
}
