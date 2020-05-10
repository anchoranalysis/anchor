package org.anchoranalysis.image.voxel.kernel.outline;

import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.kernel.dilateerode.BinaryKernelMorph3Extent;

public abstract class OutlineKernel3Base extends BinaryKernelMorph3Extent {
	
	/** Disconsiders anything outside the threshold. Takes priority ahead of outsideAtThreshold */
	protected final boolean ignoreAtThreshold;
	
	public OutlineKernel3Base(BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ, boolean ignoreAtThreshold) {
		super(bv, outsideAtThreshold, useZ);
		this.ignoreAtThreshold = ignoreAtThreshold;
	}
}
