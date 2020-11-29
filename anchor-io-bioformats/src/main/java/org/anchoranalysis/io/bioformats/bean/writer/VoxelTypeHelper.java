/*-
 * #%L
 * anchor-plugin-io
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
package org.anchoranalysis.io.bioformats.bean.writer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ome.xml.model.enums.PixelType;
import org.anchoranalysis.core.functional.checked.CheckedRunnable;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class VoxelTypeHelper {

    public static PixelType pixelTypeFor(VoxelDataType dataType) throws ImageIOException {
        if (dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return PixelType.UINT8;
        } else if (dataType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return PixelType.UINT16;
        } else if (dataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return PixelType.UINT32;
        } else if (dataType.equals(FloatVoxelType.INSTANCE)) {
            return PixelType.FLOAT;
        } else {
            throw new ImageIOException(
                    String.format("%s is an unsupported data-type for this writer", dataType));
        }
    }

    public static void checkChannelTypeSupported(
            String messagePrefix,
            VoxelDataType channelType,
            CheckedRunnable<ImageIOException> runnable)
            throws ImageIOException {
        if (isChannelTypeSupported(channelType)) {
            runnable.run();
        } else {
            throw new ImageIOException(messagePrefix + "an unsupported type: " + messagePrefix);
        }
    }

    private static boolean isChannelTypeSupported(VoxelDataType channelType) {
        return channelType.equals(UnsignedByteVoxelType.INSTANCE)
                || channelType.equals(UnsignedShortVoxelType.INSTANCE)
                || channelType.equals(UnsignedIntVoxelType.INSTANCE)
                || channelType.equals(FloatVoxelType.INSTANCE);
    }
}
