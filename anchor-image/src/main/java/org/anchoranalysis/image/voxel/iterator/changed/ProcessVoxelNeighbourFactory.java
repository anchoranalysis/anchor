package org.anchoranalysis.image.voxel.iterator.changed;

import java.util.Optional;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;

public class ProcessVoxelNeighbourFactory {

	private ProcessVoxelNeighbourFactory() {}
	
	/**
	 * Within either a mask or an extent (as a fallback)
	 * 
	 * @param containingMask if defined, the process is restricted to only process points within this mask
	 * @param extntFallback if {@link containingMask} is not defined, then as a fallback, the process is restricted to only process points in this extent
	 * @param process a process which will be wrapped inside a restriction
	 * @return a new process with a restriction on the existing process
	 */
	public static ProcessVoxelNeighbour within(Optional<ObjMask> containingMask, Extent extntFallback, ProcessVoxelNeighbourAbsolute process) {
		return containingMask.map( mask->
			withinMask(mask, process)
		).orElseGet( ()->
			withinExtent(extntFallback, process)
		);
	}
	
	public static ProcessVoxelNeighbour withinExtent(Extent extnt, ProcessVoxelNeighbourAbsolute process) {
		return new WithinExtent(extnt, process);
	}
	
	public static ProcessVoxelNeighbour withinMask(ObjMask om, ProcessChangedPointAbsoluteMasked process) {
		return new WithinMask(process, om);
	}
	
	public static ProcessVoxelNeighbour withinMask(ObjMask om, ProcessVoxelNeighbourAbsolute process) {
		return new WithinMask(
			new WrapAbsoluteAsMasked(process),
			om
		);
	}
}
