package org.anchoranalysis.image.voxel.iterator.changed;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Optional;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

public class ProcessVoxelNeighbourFactory {

	private ProcessVoxelNeighbourFactory() {}
	
	/**
	 * Within either a mask or an extent (as a fallback)
	 * 
	 * @param containingMask if defined, the process is restricted to only process points within this mask
	 * @param extentFallback if {@code containingMask} is not defined, then as a fallback, the process is restricted to only process points in this extent
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
