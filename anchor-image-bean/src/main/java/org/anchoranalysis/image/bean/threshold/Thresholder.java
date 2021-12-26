/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.bean.threshold;

import java.util.Optional;
import org.anchoranalysis.bean.NullParametersBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Thresholds voxels to create a binary-voxels using <a href="https://www.quora.com/What-is-global-thresholding-in-image-processing">global thresholding</a>.
 *
 * @author Owen Feehan
 */
public abstract class Thresholder extends NullParametersBean<VoxelsThresholder> {

    /**
     * Like {@link #threshold(VoxelsUntyped)} but using default binary-values.
     *
     * <p>The default values are <i>off</i>(0) and <i>on</i>(255).
     *
     * @param voxels the voxels to be thresholded.
     * @return a binary-channel as described above, which may possibly reuse the input
     *     voxel-buffers.
     * @throws OperationFailedException if the thresholding operation cannot complete successfully.
     */
    public BinaryVoxels<UnsignedByteBuffer> threshold(VoxelsUntyped voxels)
            throws OperationFailedException {
        return threshold(voxels, BinaryValuesByte.getDefault());
    }

    /**
     * Like {@link #threshold(VoxelsUntyped, BinaryValuesByte, Optional, Optional)} applying the
     * thresholding to the entire set of voxels.
     *
     * <p>The thresholder does not accept a histogram as input.
     *
     * @param voxels the voxels to be thresholded.
     * @param binaryValues what binary values to be used in the output.
     * @return a binary-channel as described above, which may possibly reuse the input
     *     voxel-buffers.
     * @throws OperationFailedException if the thresholding operation cannot complete successfully.
     */
    public BinaryVoxels<UnsignedByteBuffer> threshold(
            VoxelsUntyped voxels, BinaryValuesByte binaryValues) throws OperationFailedException {
        return threshold(voxels, binaryValues, Optional.empty(), Optional.empty());
    }

    /**
     * Thresholds voxels (across a range of values) so that they have only binary range (i.e. two
     * voxel values representing <i>on</i> and <i>off</i>).
     *
     * <p>If a mask is used, the voxels outside the object-mask are left unchanged. They will be
     * either identical to the input-volume or 0 if a new buffer needs to be created.
     *
     * @param voxels the voxels to be thresholded.
     * @param binaryValues what binary values to be used in the output.
     * @param histogram a histogram if it's available, which must exactly match the intensity-values
     *     of {@code voxels} after any object-mask is applied. This exists for calculation
     *     efficiency.
     * @param objectMask an object-mask to restrict thresholding to only some region(s) of the
     *     voxels.
     * @return a binary-channel as described above, which may possibly reuse the input
     *     voxel-buffers.
     * @throws OperationFailedException if the thresholding operation cannot complete successfully.
     */
    public abstract BinaryVoxels<UnsignedByteBuffer> threshold(
            VoxelsUntyped voxels,
            BinaryValuesByte binaryValues,
            Optional<Histogram> histogram,
            Optional<ObjectMask> objectMask)
            throws OperationFailedException;
}
