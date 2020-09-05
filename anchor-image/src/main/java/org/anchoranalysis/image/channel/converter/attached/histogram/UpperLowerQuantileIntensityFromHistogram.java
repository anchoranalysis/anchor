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

package org.anchoranalysis.image.channel.converter.attached.histogram;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.converter.ConversionPolicy;
import org.anchoranalysis.image.channel.converter.ToUnsignedByte;
import org.anchoranalysis.image.channel.converter.attached.ChannelConverterAttached;
import org.anchoranalysis.image.channel.converter.voxels.ConvertToByteScaleByMinMaxValue;
import org.anchoranalysis.image.channel.converter.voxels.VoxelsConverter;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.histogram.Histogram;

/**
 * Scales by a lower and upper quantile of the intensity values of an image
 *
 * @author Owen Feehan
 */
public class UpperLowerQuantileIntensityFromHistogram
        implements ChannelConverterAttached<Histogram, UnsignedByteBuffer> {

    private ConvertToByteScaleByMinMaxValue voxelsConverter;
    private double quantileLower = 0.0;
    private double quantileUpper = 1.0;
    private double scaleLower = 0.0;
    private double scaleUpper = 0.0;
    private ToUnsignedByte delegate;

    public UpperLowerQuantileIntensityFromHistogram(double quantileLower, double quantileUpper) {
        this(quantileLower, quantileUpper, 1.0, 1.0);
    }

    public UpperLowerQuantileIntensityFromHistogram(
            double quantileLower, double quantileUpper, double scaleLower, double scaleUpper) {
        // Initialize with a dummy value
        voxelsConverter = new ConvertToByteScaleByMinMaxValue(0, 1);
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
