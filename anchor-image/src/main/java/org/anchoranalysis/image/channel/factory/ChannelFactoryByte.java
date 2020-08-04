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

package org.anchoranalysis.image.channel.factory;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromByteBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChannelFactoryByte implements ChannelFactorySingleType {

    private static Log log = LogFactory.getLog(ChannelFactoryByte.class);
    private static final VoxelDataTypeUnsignedByte DATA_TYPE = VoxelDataTypeUnsignedByte.INSTANCE;

    private static final VoxelBoxFactoryTypeBound<ByteBuffer> FACTORY = VoxelBoxFactory.getByte();

    @Override
    public Channel createEmptyInitialised(ImageDimensions dim) {
        VoxelBox<ByteBuffer> vb = FACTORY.create(dim.getExtent());

        log.debug(String.format("Creating empty initialised: %s", dim.getExtent().toString()));

        return create(vb, dim.getResolution());
    }

    @Override
    public Channel createEmptyUninitialised(ImageDimensions dimensions) {
        PixelsFromByteBufferArr pixels =
                PixelsFromByteBufferArr.createEmpty(dimensions.getExtent());

        VoxelBox<ByteBuffer> vb = FACTORY.create(pixels);
        return create(vb, dimensions.getResolution());
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }

    public static VoxelDataType staticDataType() {
        return DATA_TYPE;
    }
}
