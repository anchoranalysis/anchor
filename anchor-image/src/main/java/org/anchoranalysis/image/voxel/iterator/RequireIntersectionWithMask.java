package org.anchoranalysis.image.voxel.iterator;

import java.nio.ByteBuffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;

/**
 * Only processes a point if it lines on the region of an Object-Mask
 * 
 * <p>Any points lying outside the object-mask are never processed.</p>
 * 
 * @author Owen Feehan
 *
 */
final class RequireIntersectionWithMask implements ProcessVoxel {

	private final ProcessVoxel process;
	
	private final ObjMask mask;
	private final Extent extent;
	private final byte byteOn;
	private final ReadableTuple3i crnrMin;
	
	private ByteBuffer bbMask;
	
	/**
	 * Constructor
	 * 
	 * @param process the processor to call on the region of the mask
	 * @param mask the mask that defines the "on" region which is processed only.
	 */
	public RequireIntersectionWithMask(ProcessVoxel process, ObjMask mask) {
		super();
		this.process = process;
		this.mask = mask;
		this.extent = mask.getVoxelBox().extent();
		this.byteOn = mask.getBinaryValuesByte().getOnByte();
		this.crnrMin = mask.getBoundingBox().getCrnrMin();
	}		
						
	@Override
	public void notifyChangeZ(int z) {
		process.notifyChangeZ(z);
		bbMask = mask.getVoxelBox().getPixelsForPlane(z - crnrMin.getZ()).buffer();
	}
	
	@Override
	public void notifyChangeY(int y) {
		process.notifyChangeY(y);
	}
	
	@Override
	public void process(Point3i pnt) {
		// We skip if our containing mask doesn't include it
		if (isPointOnMask(pnt)) {
			process.process(pnt);
		}
	}
	
	private boolean isPointOnMask(Point3i pnt) {
		int offsetMask = extent.offset(
			pnt.getX() - crnrMin.getX(),
			pnt.getY() - crnrMin.getY()
		);
		
		// We skip if our containing mask doesn't include it
		return (bbMask.get(offsetMask)==byteOn);
	}
}