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
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.nonbean.segment.BinarySegmentationParameters;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;

public abstract class BinarySegmentationOne extends BinarySegmentation {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private BinarySegmentation segment;
    // END BEAN PROPERTIES

    @Override
    public BinaryVoxels<UnsignedByteBuffer> segment(
            VoxelsWrapper voxels,
            BinarySegmentationParameters params,
            Optional<ObjectMask> objectMask)
            throws SegmentationFailedException {
        return segmentFromExistingSegmentation(voxels, params, objectMask, segment);
    }

    protected abstract BinaryVoxels<UnsignedByteBuffer> segmentFromExistingSegmentation(
            VoxelsWrapper voxels,
            BinarySegmentationParameters params,
            Optional<ObjectMask> object,
            BinarySegmentation sgmn)
            throws SegmentationFailedException;
}
