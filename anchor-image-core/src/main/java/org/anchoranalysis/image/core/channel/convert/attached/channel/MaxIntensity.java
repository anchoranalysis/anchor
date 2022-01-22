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
import org.anchoranalysis.image.core.channel.convert.ConversionPolicy;
import org.anchoranalysis.image.core.channel.convert.ToUnsignedByte;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.convert.ToUnsignedByteScaleByMaxValue;
import org.anchoranalysis.image.voxel.convert.VoxelsConverter;

/**
 * Converts a {@link Channel} to {@link UnsignedByteBuffer} by scaling against the <b>maximum
 * intensity value</b> that appears in it.
 *
 * <p>Specifically, the range is from 0 to {@code max(intensity)} across all voxels.
 *
 * @author Owen Feehan
 */
public class MaxIntensity implements ChannelConverterAttached<Channel, UnsignedByteBuffer> {

    private ToUnsignedByteScaleByMaxValue voxelsConverter;

    private ToUnsignedByte delegate;

    /** Default constructor. */
    public MaxIntensity() {
        // Initialize with a dummy value
        voxelsConverter = new ToUnsignedByteScaleByMaxValue(1);

        delegate = new ToUnsignedByte(voxelsConverter);
    }

    @Override
    public void attachObject(Channel channel) {
        voxelsConverter.setMaxValue(channel.extract().voxelWithMaxIntensity());
    }

    @Override
    public Channel convert(Channel channel, ConversionPolicy changeExisting) {
        return delegate.convert(channel, changeExisting);
    }

    @Override
    public VoxelsConverter<UnsignedByteBuffer> getVoxelsConverter() {
        return voxelsConverter;
    }
}
