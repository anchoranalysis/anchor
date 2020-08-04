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

package org.anchoranalysis.image.stack.region.chnlconverter.attached.chnl;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverterToUnsignedByte;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelsConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.ToByteScaleByMaxValue;

public class ChnlConverterChnlMaxIntensity implements ChnlConverterAttached<Channel, ByteBuffer> {

    private ToByteScaleByMaxValue voxelsConverter;

    private ChannelConverterToUnsignedByte delegate;

    public ChnlConverterChnlMaxIntensity() {
        // Initialise with a dummy value
        voxelsConverter = new ToByteScaleByMaxValue(1);

        delegate = new ChannelConverterToUnsignedByte(voxelsConverter);
    }

    @Override
    public void attachObject(Channel obj) {

        int maxValue = obj.voxels().any().ceilOfMaxPixel();
        voxelsConverter.setMaxValue(maxValue);
    }

    @Override
    public Channel convert(Channel chnl, ConversionPolicy changeExisting) {
        return delegate.convert(chnl, changeExisting);
    }

    @Override
    public VoxelsConverter<ByteBuffer> getVoxelsConverter() {
        return voxelsConverter;
    }
}
