package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;

/**
 * Converts a {@link ProcessVoxel} into a {@link ProcessVoxelOffsets}
 * 
 * <p>Note that {@link} notifyChangeZ need not be be called for all slices (perhaps only a subset), but {@link process} must be called
 * for ALL voxels on a given slice</p>.
 * 
 * @author Owen Feehan
 *
 */
public final class AddOffsets implements ProcessVoxel {

	private final ProcessVoxelOffsets delegate;
	private final Extent extent;
	
	/** A 3D offset for the 0th pixel in the current slice. */
	private int offsetForSlice3D = 0;
	
	/** A 2D offset within the current slice */
	private int offsetWithinSlice;
	
	public AddOffsets(ProcessVoxelOffsets process, Extent extent) {
		this.delegate = process;
		this.extent = extent;
	}
	
	@Override
	public void notifyChangeZ(int z) {
		offsetWithinSlice = 0;
		offsetForSlice3D = extent.offset(0, 0, z);
		delegate.notifyChangeZ(z);
	}
	
	@Override
	public void process(Point3i pnt) {
		delegate.process(
			pnt,
			offsetForSlice3D + offsetWithinSlice,
			offsetWithinSlice
		);
		offsetWithinSlice++;
	}
}
