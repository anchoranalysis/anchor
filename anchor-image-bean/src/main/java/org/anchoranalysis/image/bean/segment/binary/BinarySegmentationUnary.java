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

package org.anchoranalysis.image.bean.segment.binary;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.segment.BinarySegmentationParameters;
import org.anchoranalysis.image.bean.nonbean.segment.SegmentationFailedException;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * An implementation of {@link BinarySegmentation} that delegates to one other {@link
 * BinarySegmentation}.
 *
 * @author Owen Feehan
 */
public abstract class BinarySegmentationUnary extends BinarySegmentation {

    // START BEAN PROPERTIES
    /** The delegate {@link BinarySegmentation} that may be called. */
    @BeanField @Getter @Setter private BinarySegmentation segment;

    // END BEAN PROPERTIES

    @Override
    public BinaryVoxels<UnsignedByteBuffer> segment(
            VoxelsUntyped voxels,
            BinarySegmentationParameters parameters,
            Optional<ObjectMask> objectMask)
            throws SegmentationFailedException {
        return segmentFromExistingSegmentation(voxels, parameters, objectMask, segment);
    }

    /**
     * Performs a binary-segmentation, in a similar manner to {@link BinarySegmentation#segment} but
     * with the delegate as additional argument.
     *
     * @param voxels voxels to segment.
     * @param parameters parameters to guide the algorithm.
     * @param objectMask if present, segmentation only occurs inside this object.
     * @param segment the delegate {@link BinarySegmentation}.
     * @return voxels for a mask on the input-buffer, which may be newly-created, or may reuse the
     *     input {@code voxels}, depending on implementation.
     * @throws SegmentationFailedException if the segmentation cannot be successfully completed.
     */
    protected abstract BinaryVoxels<UnsignedByteBuffer> segmentFromExistingSegmentation(
            VoxelsUntyped voxels,
            BinarySegmentationParameters parameters,
            Optional<ObjectMask> objectMask,
            BinarySegmentation segment)
            throws SegmentationFailedException;
}
