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

import org.anchoranalysis.image.channel.converter.voxels.ConvertToByteNoScaling;
import org.anchoranalysis.image.channel.converter.voxels.VoxelsConverter;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

/**
 * Converts from other data types to {@link UnsignedByteBuffer} (unsigned 8-bit) without scaling.
 *
 * <p>This implies undefined behaviour for values in the source data-type that lie outside the range
 * of {@link UnsignedByteBuffer}.
 *
 * @author Owen Feehan
 */
public class ToUnsignedByte extends ChannelConverter<UnsignedByteBuffer> {

    public ToUnsignedByte() {
        this(new ConvertToByteNoScaling());
    }

    public ToUnsignedByte(VoxelsConverter<UnsignedByteBuffer> voxelsConverter) {
        super(UnsignedByteVoxelType.INSTANCE, voxelsConverter, VoxelsFactory.getByte());
    }
}
