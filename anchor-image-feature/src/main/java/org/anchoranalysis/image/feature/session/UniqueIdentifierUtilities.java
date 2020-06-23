package org.anchoranalysis.image.feature.session;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.objectmask.ObjectMask;

/**
 * Generates unique identifiers for object-masks
 * 
 * @author Owen Feehan
 *
 */
public class UniqueIdentifierUtilities {

	private UniqueIdentifierUtilities() {}
	
	/**
	 * Generates a unique identifier (unique within a particular collection) for an object based upon
	 *  an assumption that there are no overlapping objects in this collection.
	 * 
	 * @param obj object to generate identifier for
	 * @return a string encoded with an arbitrary point that lies on the object, or "none" if the object has no points
	 */
	public static String forObject(ObjectMask obj) {
		return obj.findArbitraryOnVoxel().map(
			UniqueIdentifierUtilities::forPoint
		).orElse("none");
	}
	
	private static String forPoint(Point3i pnt) {
		return String.format("%d_%d_%d", pnt.getX(), pnt.getY(), pnt.getZ());
	}
}
