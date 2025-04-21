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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.object.HistogramFromObjectsFactory;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Performs global thresholding.
 *
 * <p>This implies that the threshold-level is identical for every voxel.
 *
 * <p>The thresholding occurs inplace on the existing voxels i.e. a new buffer is not created.
 *
 * <p>An <i>on</i> voxel is placed in the buffer if {@code voxel-value >= level} or <i>off</i>
 * otherwise.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class ThresholderGlobal extends Thresholder {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private CalculateLevel calculateLevel;

    // END BEAN PARAMETERS

    @Override
    public BinaryVoxels<UnsignedByteBuffer> threshold(
            VoxelsUntyped inputBuffer,
            BinaryValuesByte bvOut,
            Optional<Histogram> histogram,
            Optional<ObjectMask> objectMask)
            throws OperationFailedException {
        return thresholdForHistogram(
                histogramBuffer(inputBuffer, histogram, objectMask),
                inputBuffer,
                bvOut,
                objectMask);
    }

    private BinaryVoxels<UnsignedByteBuffer> thresholdForHistogram(
            Histogram histogram,
            VoxelsUntyped inputBuffer,
            BinaryValuesByte bvOut,
            Optional<ObjectMask> objectMask)
            throws OperationFailedException {

        int thresholdVal = calculateLevel.calculateLevel(histogram);
        assert (thresholdVal >= 0);
        return VoxelsThresholder.threshold(inputBuffer, thresholdVal, bvOut, objectMask, false);
    }

    private Histogram histogramBuffer(
            VoxelsUntyped inputBuffer,
            Optional<Histogram> histogram,
            Optional<ObjectMask> objectMask) {
        return histogram.orElseGet(
                () -> HistogramFromObjectsFactory.createFrom(inputBuffer, objectMask));
    }
}
