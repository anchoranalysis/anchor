package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;

/**
 * Slides along a {@link SlidingBuffer} as points are being processed.
 * 
 * @author Owen Feehan
 *
 */
final class ProcessPointSlide implements ProcessPoint {

	private final SlidingBuffer<?> buffer;
	private final ProcessPoint process;
	
	public ProcessPointSlide(SlidingBuffer<?> buffer, ProcessPoint process) {
		this.process = process;
		this.buffer = buffer;
	}
	
	@Override
	public void process(Point3i pnt) {
		process.process(pnt);
	}

	@Override
	public void notifyChangeZ(int z) {
		if (z!=0) {
			buffer.shift();
		}
		process.notifyChangeZ(z);
	}
}