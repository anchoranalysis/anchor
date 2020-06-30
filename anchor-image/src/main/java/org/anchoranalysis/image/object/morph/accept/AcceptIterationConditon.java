package org.anchoranalysis.image.object.morph.accept;

import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.box.VoxelBox;

/**
 * A condition that must be fulfilled for a particular iteration of a morphological operation to be accepted.
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface AcceptIterationConditon {
	/**
	 * 
	 * @param buffer
	 * @param bvb
	 * @return TRUE if the particular iteration should be accepted, FALSE otherwise
	 */
	boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) throws OperationFailedException;
}