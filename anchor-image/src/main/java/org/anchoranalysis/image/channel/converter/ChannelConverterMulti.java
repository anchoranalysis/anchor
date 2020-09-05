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

package org.anchoranalysis.image.channel.converter;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Converts a channel from one type to multiple other types.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ChannelConverterMulti {

    private static final ConversionPolicy POLICY = ConversionPolicy.DO_NOT_CHANGE_EXISTING;

    public Channel convert(Channel channel, VoxelDataType outputType) {

        if (channel.getVoxelDataType().equals(outputType)) {
            return channel;
        } else {
            return converterFor(outputType).convert(channel, POLICY);
        }
    }

    private ChannelConverter<?> converterFor(VoxelDataType outputType) {

        if (outputType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return new ToUnsignedByte();
        } else if (outputType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return new ToUnsignedShort();
        } else if (outputType.equals(FloatVoxelType.INSTANCE)) {
            return new ToFloat();
        } else if (outputType.equals(UnsignedIntVoxelType.INSTANCE)) {
            throw new UnsupportedOperationException(
                    "UnsignedInt is not yet supported for this operation");
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
