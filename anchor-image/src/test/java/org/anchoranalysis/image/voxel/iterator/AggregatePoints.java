package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.core.arithmetic.RunningSumPoint;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;

/**
 * Aggregates points as they are successively processed
 * 
 * @author Owen Feehan
 *
 */
class AggregatePoints implements ProcessVoxel {

	private RunningSumPoint runningSum = new RunningSumPoint();
	
	@Override
	public void process(Point3i pnt) {
		runningSum.increment(pnt);
	}

	public int count() {
		return runningSum.getCountXY();
	}

	/** The center-of-gravity of all points-processed (discretized) */
	public Point3i center() {
		return PointConverter.intFromDouble( runningSum.mean() );
	}
}