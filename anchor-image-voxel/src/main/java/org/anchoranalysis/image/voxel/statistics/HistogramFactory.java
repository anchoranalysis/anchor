/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.voxel.statistics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Creates a {@link Histogram} to describe the intensity values of voxels in aggregate.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramFactory {

    /**
     * Creates a {@link Histogram} of the aggregated voxel intensities in a {@link VoxelBuffer}.
     * 
     * @param buffer the buffer, whose voxel intensity values are aggregated into a {@link Histogram}.
     * @return a newly created histogram.
     */
    public static Histogram create(VoxelBuffer<?> buffer) {

        Histogram histogram = new Histogram((int) buffer.dataType().maxValue());
        addBufferToHistogram(histogram, buffer, buffer.capacity());
        return histogram;
    }

    /**
     * Creates a {@link Histogram} of the aggregated voxel intensities in a {@link VoxelsUntyped}.
     * 
     * @param voxels the {@link VoxelsUntyped}, whose voxel intensity values are aggregated into a {@link Histogram}.
     * @return a newly created histogram.
     */
    public static Histogram create(VoxelsUntyped voxels) {
        return createFromVoxels(voxels.any());
    }

    private static Histogram createFromVoxels(Voxels<?> inputBox) {

        int maxValue = (int) inputBox.dataType().maxValue();

        Histogram histogram = new Histogram(maxValue);

        int volumeXY = inputBox.extent().areaXY();

        inputBox.extent()
                .iterateOverZ(z -> addBufferToHistogram(histogram, inputBox.slice(z), volumeXY));

        return histogram;
    }

    private static void addBufferToHistogram(
            Histogram histogram, VoxelBuffer<?> buffer, int maxOffset) {
        for (int offset = 0; offset < maxOffset; offset++) {
            int val = buffer.getInt(offset);
            histogram.incrementValue(val);
        }
    }
}
