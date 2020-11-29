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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ByteRepresentationFactory {

    /**
     * Constructs a means of fetching representation of a slice of a channel in bytes for a
     * particular target destination type.
     *
     * <p>An assumption is that it will only be used for getting a byte-representation of the same
     * voxel-type or <i>upcasting</i> to a type of higher bit depth.
     *
     * @param channel the channel from which a slice is extracted to form a byte-representation
     * @param destinationType the type we wish to save the channel in the image as (and with which
     *     we need a byte representation in this form).
     * @return a means of fetching a byte-representation for a slice of a channel
     */
    public static ByteRepresentationForChannel byteRepresentationFor(
            Channel channel, VoxelDataType destinationType) {
        VoxelDataType sourceDataType = channel.getVoxelDataType();
        return sliceIndex ->
                convertTo(channel.voxels().slice(sliceIndex), sourceDataType, destinationType);
    }

    private static byte[] convertTo(
            VoxelBuffer<?> buffer, VoxelDataType sourceDataType, VoxelDataType destinationType)
            throws ImageIOException {

        if (sourceDataType.equals(destinationType)) {
            return buffer.underlyingBytes();
        }

        if (destinationType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return toUnsignedShort(buffer);
        } else if (destinationType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return toUnsignedInt(buffer);
        } else if (destinationType.equals(FloatVoxelType.INSTANCE)) {
            return toFloat(buffer);
        } else {
            throw new ImageIOException(
                    "Unsupported destination-type for representation in bytes: " + destinationType);
        }
    }

    private static byte[] toUnsignedShort(VoxelBuffer<?> in) {
        int index = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(in.capacity() * 2);
        UnsignedShortBuffer shortBuffer = UnsignedShortBuffer.wrapRaw(byteBuffer.asShortBuffer());
        while (shortBuffer.hasRemaining()) {
            shortBuffer.putUnsigned(in.getInt(index++));
        }
        return byteBuffer.array();
    }

    private static byte[] toUnsignedInt(VoxelBuffer<?> in) {
        int index = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(in.capacity() * 4);
        UnsignedIntBuffer intBuffer = UnsignedIntBuffer.wrapRaw(byteBuffer.asIntBuffer());
        while (intBuffer.hasRemaining()) {
            intBuffer.putUnsigned(in.getInt(index++));
        }
        return byteBuffer.array();
    }

    private static byte[] toFloat(VoxelBuffer<?> in) {
        int index = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(in.capacity() * 4);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        while (floatBuffer.hasRemaining()) {
            floatBuffer.put(in.getInt(index++));
        }
        return byteBuffer.array();
    }
}
