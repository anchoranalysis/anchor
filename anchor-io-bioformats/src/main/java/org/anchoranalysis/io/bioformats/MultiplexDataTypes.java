/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats;

import loci.formats.FormatTools;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactoryFloat;
import org.anchoranalysis.image.channel.factory.ChannelFactoryInt;
import org.anchoranalysis.image.channel.factory.ChannelFactoryShort;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.Float;
import org.anchoranalysis.image.voxel.datatype.SignedShort;
import org.anchoranalysis.image.voxel.datatype.UnsignedByte;
import org.anchoranalysis.image.voxel.datatype.UnsignedInt;
import org.anchoranalysis.image.voxel.datatype.UnsignedShort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MultiplexDataTypes {

    public static VoxelDataType multiplexFormat(int pixelType) throws RasterIOException {
        switch (pixelType) {
            case FormatTools.UINT8:
                return UnsignedByte.INSTANCE;
            case FormatTools.UINT16:
                return UnsignedShort.INSTANCE;
            case FormatTools.INT16:
                return SignedShort.instance;
            case FormatTools.FLOAT:
                return Float.INSTANCE;
            default:
                throw new RasterIOException(
                        String.format(
                                "File has unknown type %s",
                                FormatTools.getPixelTypeString(pixelType)));
        }
    }

    public static ChannelFactorySingleType multiplexVoxelDataType(VoxelDataType voxelDataType) {
        if (voxelDataType.equals(UnsignedByte.INSTANCE)) {
            return new ChannelFactoryByte();
        } else if (voxelDataType.equals(UnsignedShort.INSTANCE)) {
            return new ChannelFactoryShort();
        } else if (voxelDataType.equals(SignedShort.instance)) {
            return new ChannelFactoryShort();
        } else if (voxelDataType.equals(Float.INSTANCE)) {
            return new ChannelFactoryFloat();
        } else if (voxelDataType.equals(UnsignedInt.INSTANCE)) {
            return new ChannelFactoryInt();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
