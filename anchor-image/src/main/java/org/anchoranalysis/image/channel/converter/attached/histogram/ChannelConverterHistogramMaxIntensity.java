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

import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.converter.ChannelConverterToUnsignedByte;
import org.anchoranalysis.image.channel.converter.ConversionPolicy;
import org.anchoranalysis.image.channel.converter.attached.ChannelConverterAttached;
import org.anchoranalysis.image.channel.converter.voxels.ConvertToByteScaleByMaxValue;
import org.anchoranalysis.image.channel.converter.voxels.VoxelsConverter;
import org.anchoranalysis.image.histogram.Histogram;

public class ChannelConverterHistogramMaxIntensity
        implements ChannelConverterAttached<Histogram, ByteBuffer> {

    private ConvertToByteScaleByMaxValue voxelsConverter;

    private ChannelConverterToUnsignedByte delegate;

    public ChannelConverterHistogramMaxIntensity() {
        // Initialise with a dummy value
        voxelsConverter = new ConvertToByteScaleByMaxValue(1);

        delegate = new ChannelConverterToUnsignedByte(voxelsConverter);
    }

    @Override
    public void attachObject(Histogram hist) throws OperationFailedException {

        int maxValue = hist.calculateMaximum();
        voxelsConverter.setMaxValue(maxValue);
    }

    @Override
    public Channel convert(Channel channel, ConversionPolicy changeExisting) {
        return delegate.convert(channel, changeExisting);
    }

    @Override
    public VoxelsConverter<ByteBuffer> getVoxelsConverter() {
        return voxelsConverter;
    }
}
