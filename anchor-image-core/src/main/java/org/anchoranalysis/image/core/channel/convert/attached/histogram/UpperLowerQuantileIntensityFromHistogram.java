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

package org.anchoranalysis.image.core.channel.convert.attached.histogram;

import com.google.common.base.Preconditions;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.ConversionPolicy;
import org.anchoranalysis.image.core.channel.convert.ToUnsignedByte;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.convert.ToByteScaleByMinMaxValue;
import org.anchoranalysis.image.voxel.convert.VoxelsConverter;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Converts a {@link Channel} to {@link UnsignedByteBuffer} by scaling against lower and upper
 * <b>quantiles</b> of the intensity values from a corresponding histogram.
 *
 * <p>A scaling-factor may also be applied to each limit.
 *
 * <p>Specifically, the range is from {@code scaleLower * calculate_quantile(intensity,
 * quantileLower)} to {@code scaleUpper * calculate_quantile(intensity, quantileUpper)} across all
 * voxels.
 *
 * @author Owen Feehan
 */
public class UpperLowerQuantileIntensityFromHistogram
        implements ChannelConverterAttached<Histogram, UnsignedByteBuffer> {

    private ToByteScaleByMinMaxValue voxelsConverter;
    private double quantileLower = 0.0;
    private double quantileUpper = 1.0;
    private double scaleLower = 0.0;
    private double scaleUpper = 0.0;
    private ToUnsignedByte delegate;

    /**
     * Scale with quantile values for the lower and upper boundaries - without any scaling factors.
     *
     * @param quantileLower quantile that defines the <b>lower</b> boundary.
     * @param quantileUpper quantile that defines the <b>upper</b> boundary.
     */
    public UpperLowerQuantileIntensityFromHistogram(double quantileLower, double quantileUpper) {
        this(quantileLower, quantileUpper, 1.0, 1.0);
    }

    /**
     * Scale with quantile values for the lower and upper boundaries - with explicit scaling
     * factors.
     *
     * @param quantileLower quantile that defines the <b>lower</b> boundary.
     * @param quantileUpper quantile that defines the <b>upper</b> boundary.
     * @param scaleLower scaling factor for the <b>lower</b> boundary.
     * @param scaleUpper scaling factor for the <b>upper</b> boundary.
     */
    public UpperLowerQuantileIntensityFromHistogram(
            double quantileLower, double quantileUpper, double scaleLower, double scaleUpper) {
        Preconditions.checkArgument(quantileLower >= 0 && quantileLower <= 1);
        Preconditions.checkArgument(quantileUpper >= 0 && quantileUpper <= 1);
        Preconditions.checkArgument(quantileUpper > quantileLower);
        // Initialize with a dummy value
        voxelsConverter = new ToByteScaleByMinMaxValue(0, 1);
        this.quantileLower = quantileLower;
        this.quantileUpper = quantileUpper;
        this.scaleUpper = scaleUpper;
        this.scaleLower = scaleLower;

        delegate = new ToUnsignedByte(voxelsConverter);
    }

    @Override
    public void attachObject(Histogram histogram) throws OperationFailedException {

        int minValue = scaleQuantile(histogram, quantileLower, scaleLower);
        int maxValue = scaleQuantile(histogram, quantileUpper, scaleUpper);
        voxelsConverter.setMinMaxValues(minValue, maxValue);
    }

    @Override
    public Channel convert(Channel channel, ConversionPolicy changeExisting) {
        return delegate.convert(channel, changeExisting);
    }

    @Override
    public VoxelsConverter<UnsignedByteBuffer> getVoxelsConverter() {
        return voxelsConverter;
    }

    private int scaleQuantile(Histogram histogram, double quantile, double scaleFactor)
            throws OperationFailedException {
        return (int) Math.round(histogram.quantile(quantile) * scaleFactor);
    }
}
