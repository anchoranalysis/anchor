package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.core.geometry.Point3i;

/**
 * Processes a 3D point
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface ProcessPoint {
	
	/** Notifies the processor that there has been a change in z-coordinate */
	default void notifyChangeZ(int z) {}
	
	void process(Point3i pnt);
}