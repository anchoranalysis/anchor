/* (C)2020 */
package org.anchoranalysis.image.bean.threshold;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;

/**
 * Thresholds a voxel-box to create a binary-voxel-box
 *
 * @author Owen Feehan
 */
public abstract class Thresholder extends NullParamsBean<VoxelBoxThresholder> {

    /**
     * Thresholds voxels (across a range of values) so that they have only binary range (i.e. two
     * voxel values representing ON and OFF)
     *
     * <p>If a mask is used, the voxels outside the mask are left unchanged. They will be either
     * identical to the input-volume or 0 if a new buffer needs to be created.</p.
     *
     * @param voxelBox the voxels to be thresholded
     * @param binaryValues what binary values to be used in the output
     * @param histogram a histogram if it's available, which must exactly match the intensity-values
     *     of {@link voxels} after any mask is applied. This exists for calculation efficiency.
     * @param mask a mask to restrict thresholding to only some region(s) of the voxel-box
     * @return a binary-channel as described above, which may possibly reuse the input voxel-buffers
     *     which should be reused.
     * @throws OperationFailedException
     */
    public abstract BinaryVoxelBox<ByteBuffer> threshold(
            VoxelBoxWrapper voxelBox,
            BinaryValuesByte binaryValues,
            Optional<Histogram> histogram,
            Optional<ObjectMask> mask)
            throws OperationFailedException;
}
