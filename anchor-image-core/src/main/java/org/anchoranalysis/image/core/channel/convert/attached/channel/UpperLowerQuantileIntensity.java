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

package org.anchoranalysis.image.core.channel.convert.attached.channel;

import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.attached.histogram.UpperLowerQuantileIntensityFromHistogram;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Converts a {@link Channel} to {@link UnsignedByteBuffer} by scaling against lower and upper
 * <b>quantiles</b> of the intensity values that appear in it.
 *
 * <p>Specifically, the range is from {@code calculate_quantile(intensity, quantileLower)} to {@code
 * calculate_quantile(intensity, quantileUpper)} across all voxels.
 *
 * @author Owen Feehan
 */
public class UpperLowerQuantileIntensity extends DelegateToHistogram<UnsignedByteBuffer> {

    /**
     * Scale with quantile values for the lower and upper boundaries.
     *
     * @param quantileLower quantile that defines the <b>lower</b> boundary.
     * @param quantileUpper quantile that defines the <b>upper</b> boundary.
     */
    public UpperLowerQuantileIntensity(double quantileLower, double quantileUpper) {
        super(new UpperLowerQuantileIntensityFromHistogram(quantileLower, quantileUpper));
    }
}
