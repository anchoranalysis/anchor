package org.anchoranalysis.image.extent;

import org.anchoranalysis.core.geometry.Point3i;


/**
 * Performs union of a bounding-box with other entities
 * 
 * @author Owen Feehan
 *
 */
public class BoundingBoxUnion {

private final BoundingBox bbox;
	
	public BoundingBoxUnion(BoundingBox bbox) {
		super();
		this.bbox = bbox;
	}
	
	/**
	 * Performs a union with another box (immutably)
	 * 
	 * @param other the other bounding box
	 * @return a new bounding-box that is union of both bounding boxes
	 */
	public BoundingBox with( BoundingBox other ) {
		
		Point3i crnrMin = bbox.getCrnrMin();
		Point3i crnrMinOther = other.getCrnrMin();
		
		Point3i crnrMax = bbox.calcCrnrMax();
		Point3i crnrMaxOthr = other.calcCrnrMax();
		
		ExtentBoundsComparer meiX = ExtentBoundsComparer.createMax(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getX() );
		ExtentBoundsComparer meiY = ExtentBoundsComparer.createMax(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getY() );
		ExtentBoundsComparer meiZ = ExtentBoundsComparer.createMax(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getZ() );

		return new BoundingBox(
			new Point3i(
				meiX.getMin(),
				meiY.getMin(),
				meiZ.getMin()
			),
			new Extent(
				meiX.getExtnt(),
				meiY.getExtnt(),
				meiZ.getExtnt()
			)
		);
	}
}
