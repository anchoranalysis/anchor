package org.anchoranalysis.image.points;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;

/** 
 * Keeps track of the range experienced by points in the x, y and z dimensions
 * 
 * <p>Points can be dynamically added and the min/max is updated continuously</p>
 * 
 * @author owen
 * 
 *  */
public final class PointRange {

	private Point3i max;
	private Point3i min;
		
	public void add(Point3i pnt) {
		add(pnt.getX(), pnt.getY(), pnt.getZ());
	}
	
	public void add(int x, int y, int z) {
		
		if (max==null || min==null) {
			max = new Point3i(x,y,z);
			min = new Point3i(x,y,z);
			return;
		}
		
		if (x<min.getX()) {
			min.setX(x);
		} else if (x>max.getX()) {
			max.setX(x);
		}
		
		if (y<min.getY()) {
			min.setY(y);
		} else if (y>max.getY()) {
			max.setY(y);
		}
		
		if (z<min.getZ()) {
			min.setZ(z);
		} else if (z>max.getZ()) {
			max.setZ(z);
		}
	}
	
	public BoundingBox deriveBoundingBox() throws OperationFailedException {
		
		if (min==null || max==null) {
			throw new OperationFailedException("No point has been added, so no bounding-box can be derived");
		}
		
		return deriveBoundingBoxNoCheck();
	}
	
	/** Like {@link deriveBoundingBox} but doesn't throw an exception if no points exist. Consider calling {@link isEmpty} first to check. */
	public BoundingBox deriveBoundingBoxNoCheck() {
		return new BoundingBox(min, max);
	}
	
	/** Has a valid min and max defined? In other words, has at least one point been added? */
	public boolean isEmpty() {
		return (min==null) || (max==null);
	}
}
