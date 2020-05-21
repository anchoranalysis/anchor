package org.anchoranalysis.image.voxel.iterator;

import java.util.Optional;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;

/**
 * Iterate over voxels in an extent/bounding-box/mask calling a processor on each selected voxel
 * 
 * @author Owen Feehan
 *
 */
public class IterateVoxels {
	
	private IterateVoxels() {}

	/** 
	 * Iterate over each voxel that is located on a mask AND optionally a second-mask
	 * 
	 * <p>If a second-mask is defined, it is a logical AND condition. A voxel is only processed if it exists in both masks</p>.
	 * 
	 * @param firstMask the first-mask that is used as a condition on what voxels to iterate
	 * @param secondMask an optional second-mask that can be a further condition
	 * @param process is called for each voxel with that satisfies the conditions using GLOBAL co-ordinates for each voxel.
	 **/
	public static void overMasks( ObjMask firstMask, Optional<ObjMask> secondMask, ProcessPoint process ) {
		if (secondMask.isPresent()) {
			BoundingBox boxIntersection = firstMask.getBoundingBox().intersectCreateNewNoClip(secondMask.get().getBoundingBox());
			callEachPoint(
				boxIntersection,	// Intersection between the two bounding boxes
				requireIntersectionTwice(process, firstMask, secondMask.get())
			);
		} else {
			callEachPoint( firstMask, process );
		}
	}
	
	
	/**
	 * Iterate over each voxel that is located on a mask if it exists, otherwise iterate over the entire extent
	 * 
	 * @param mask an optional mask that is used as a condition on what voxels to iterate
	 * @param extent if mask isn't defined, then all the voxels in this {@link Extent} are iterated over instead
	 * @param process process is called for each voxel (on the entire {@link Extent} or on the object-mask depending) using GLOBAL coordinates.
	 */
	public static void callEachPoint( Optional<ObjMask> mask, Extent extent, ProcessPoint process ) {
		if (mask.isPresent()) {
			callEachPoint(mask.get(), process);
		} else {
			callEachPoint(extent, process);
		}
	}
	
	/**
	 * Iterate over each voxel that is located on a mask
	 * 
	 * @param mask the mask that is used as a condition on what voxels to iterate
	 * @param process process is called for each voxel with that satisfies the conditions using GLOBAL coordinates.
	 */
	public static void callEachPoint( ObjMask mask, ProcessPoint process ) {
		callEachPoint(
			mask.getBoundingBox(),
			new RequireIntersectionWithMask(process, mask)
		);
	}

	/**
	 * Iterate over each voxel in an {@link Extent}
	 * 
	 * @param extent the extent to be iterated over
	 * @param process process is called for each voxel inside the extent using the same coordinates as the extent.
	 */
	public static void callEachPoint( Extent extent, ProcessPoint process ) {
		callEachPoint(
			new BoundingBox(extent),
			process
		);
	}
	
	/**
	 * Iterate over each voxel in a bounding-box
	 * 
	 * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels within these bounds
	 * @param process process is called for each voxel within the bounding-box using GLOBAL coordinates.
	 */
	public static void callEachPoint( BoundingBox box, ProcessPoint process ) {
		
		Point3i crnrMin = box.getCrnrMin();
		Point3i crnrMax = box.calcCrnrMax();
		
		Point3i pnt = new Point3i();
		
		for(pnt.setZ(crnrMin.getZ()); pnt.getZ()<=crnrMax.getZ(); pnt.incrZ()) {

			process.notifyChangeZ(pnt.getZ());
			
			for(pnt.setY(crnrMin.getY()); pnt.getY()<=crnrMax.getY(); pnt.incrY()) {
				for(pnt.setX(crnrMin.getX()); pnt.getX()<=crnrMax.getX(); pnt.incrX()) {
					process.process(pnt);
				}
			}
		}
	}
		
	private static ProcessPoint requireIntersectionTwice( ProcessPoint processor, ObjMask mask1, ObjMask mask2 ) {
		ProcessPoint inner = new RequireIntersectionWithMask(processor, mask2);
		return new RequireIntersectionWithMask(inner, mask1);
	}
}