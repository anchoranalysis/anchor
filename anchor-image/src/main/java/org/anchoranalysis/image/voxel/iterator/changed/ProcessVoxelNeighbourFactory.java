package org.anchoranalysis.image.voxel.iterator.changed;

import java.util.Optional;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objectmask.ObjectMask;

public class ProcessVoxelNeighbourFactory {

	private ProcessVoxelNeighbourFactory() {}
	
	/**
	 * Within either a mask or an extent (as a fallback)
	 * 
	 * @param containingMask if defined, the process is restricted to only process points within this mask
	 * @param extentFallback if {@link containingMask} is not defined, then as a fallback, the process is restricted to only process points in this extent
	 * @param process a process which will be wrapped inside a restriction
	 * @return a new process with a restriction on the existing process
	 */
	public static<T> ProcessVoxelNeighbour<T> within(Optional<ObjectMask> containingMask, Extent extentFallback, ProcessVoxelNeighbourAbsolute<T> process) {
		return containingMask.map( mask->
			withinMask(mask, process)
		).orElseGet( ()->
			withinExtent(extentFallback, process)
		);
	}
		
	public static <T> ProcessVoxelNeighbour<T> withinExtent(ProcessVoxelNeighbourAbsoluteWithSlidingBuffer<T> process) {
		return withinExtent(
			process.getExtent(),
			process
		);
	}
	
	public static <T> ProcessVoxelNeighbour<T> withinMask(ObjectMask om, ProcessChangedPointAbsoluteMasked<T> process) {
		return new WithinMask<>(process, om);
	}
	
	public static <T> ProcessVoxelNeighbour<T> withinMask(ObjectMask om, ProcessVoxelNeighbourAbsolute<T> process) {
		return new WithinMask<>(
			new WrapAbsoluteAsMasked<>(process),
			om
		);
	}
		
	private static <T> ProcessVoxelNeighbour<T> withinExtent(Extent extent, ProcessVoxelNeighbourAbsolute<T> process) {
		return new WithinExtent<>(extent, process);
	}
}
