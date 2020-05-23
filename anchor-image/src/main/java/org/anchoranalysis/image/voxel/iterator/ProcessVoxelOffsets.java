package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.core.geometry.Point3i;

/**
 * Processes a 3D point like {@link ProcessVoxel} but also provides global (3D) and slice (2D) offsets for accessing buffers
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface ProcessVoxelOffsets {
	
	/** Notifies the processor that there has been a change in z-coordinate */
	default void notifyChangeZ(int z) {}
	
	/**
	 * Processes a point
	 * 
	 * @param pnt a point with global (absolute coordinates)
	 * @param offset3d an offset value among all slices (XYZ under a single indexing)
	 * @param offsetSlice an offset value just for the current slice (XY only under indexing)
	 */
	void process(Point3i pnt, int offset3d, int offsetSlice);
}