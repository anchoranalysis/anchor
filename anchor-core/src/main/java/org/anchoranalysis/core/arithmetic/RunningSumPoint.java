package org.anchoranalysis.core.arithmetic;

import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;

/**
 * A running sum for tracking points in each dimension
 * 
 * @author Owen Feehan
 *
 */
public final class RunningSumPoint {

	private final RunningSumCollection delegate;
	
	public RunningSumPoint() {
		delegate = new RunningSumCollection(3);
	}

	public void increment( Point2i pnt ) {
		forDim(0).increment(pnt.getX());
		forDim(1).increment(pnt.getY());
	}
	
	public void increment( Point2d pnt ) {
		forDim(0).increment(pnt.getX());
		forDim(1).increment(pnt.getY());
	}
	
	public void increment( Point3i pnt ) {
		forDim(0).increment(pnt.getX());
		forDim(1).increment(pnt.getY());
		forDim(2).increment(pnt.getZ());
	}
	
	public void increment( Point3d pnt ) {
		forDim(0).increment(pnt.getX());
		forDim(1).increment(pnt.getY());
		forDim(2).increment(pnt.getZ());
	}
	
	public Point3d mean() {
		return new Point3d(
			forDim(0).mean(),
			forDim(1).mean(),
			forDim(2).mean()
		);
	}
	
	public Point2d meanXY() {
		return new Point2d(
			forDim(0).mean(),
			forDim(1).mean()
		);
	}

	/** The count for XY dimensions (guaranteed to always be the same */
	public int getCountXY() {
		return forDim(0).getCount();
	}
	
	/** The count for Z dimension (this is identical to {@link getCountXY} if only 3D points have ben added) */
	public int getCountZ() {
		return forDim(2).getCount();
	}
	
	private RunningSum forDim( int dimIndex ) {
		return delegate.get(dimIndex);
	}
}
