package org.anchoranalysis.image.extent;

import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.geometry.Point3i;

/** 
 * Methods for checking intersection between a particular bounding-box and others
 *
 * @author owen
 **/
public final class BoundingBoxIntersection {

	private final BoundingBox bbox;
	
	public BoundingBoxIntersection(BoundingBox bbox) {
		super();
		this.bbox = bbox;
	}
	
	/** Does this bounding box intersect with another? */
	public boolean existsWith( BoundingBox other ) {
		return with(other, false).isPresent();
	}
	
	/** Does this bounding box intersection with any of the others in the list? */
	public boolean existsWithAny( List<BoundingBox> others ) {
		
		for (BoundingBox othr : others) {
			
			if (existsWith(othr)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Optional<BoundingBox> with( BoundingBox othr) {
		return with(othr, true);
	}
	
	/** Find the intersection and clip to a a containing extent */
	public Optional<BoundingBox> withInside( BoundingBox othr, Extent containingExtent) {
		return with(othr).map( bbox ->
			bbox.clipTo(containingExtent)
		);
	}
	
	/**
	 * Determines if the bounding box intersects with another, and optionally creates the bounding-box of intersection
	 * 
	 * @param other the other bounding-box to check intersection with
	 * @param createIntersectionBox iff TRUE the bounding-box of the intersection is returned, otherwise the existing (source) bounding-box is returned 
	 * @return a bounding-box if there is intersection (which box depends on {@link createIntersectionBox} or empty() if there is no intersection.
	 */
	private Optional<BoundingBox> with( BoundingBox other, boolean createIntersectionBox) {
		
		Point3i crnrMin = bbox.getCrnrMin();
		Point3i crnrMinOther = other.getCrnrMin();
		
		Point3i crnrMax = bbox.calcCrnrMax();
		Point3i crnrMaxOthr = other.calcCrnrMax();
		
		Optional<ExtentBoundsComparer> meiX = ExtentBoundsComparer.createMin(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getX() );
		Optional<ExtentBoundsComparer> meiY = ExtentBoundsComparer.createMin(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getY() );
		Optional<ExtentBoundsComparer> meiZ = ExtentBoundsComparer.createMin(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getZ() );
		
		if (!meiX.isPresent() || !meiY.isPresent() || !meiZ.isPresent()) {
			return Optional.empty();
		}
				
		if (createIntersectionBox) {
			return Optional.of(
				new BoundingBox(
					new Point3i(
						meiX.get().getMin(),
						meiY.get().getMin(),
						meiZ.get().getMin()
					),
					new Extent(
						meiX.get().getExtnt(),
						meiY.get().getExtnt(),
						meiZ.get().getExtnt()
					)
				)	
			);
		} else {
			return Optional.of(bbox);
		}
	}
}
