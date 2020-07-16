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
