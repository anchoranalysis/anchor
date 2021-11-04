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

package org.anchoranalysis.image.core.bufferedimage;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryUnsignedByte;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferWrap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateStackFromBufferedImage {

    private static final ChannelFactorySingleType FACTORY = new ChannelFactoryUnsignedByte();

    public static Stack create(BufferedImage bufferedImage) throws OperationFailedException {

        Dimensions dimensions =
                new Dimensions(bufferedImage.getWidth(), bufferedImage.getHeight(), 1);

        byte[][] arr = bytesFromBufferedImage(bufferedImage);

        try {
            return new Stack(
                    IntStream.range(0, arr.length)
                            .mapToObj(
                                    channelIndex ->
                                            createChannelFor(dimensions, arr[channelIndex])));

        } catch (IncorrectImageSizeException e) {
            throw new OperationFailedException(e);
        }
    }

    private static Channel createChannelFor(Dimensions dimensions, byte[] arr) {
        Channel channel = FACTORY.createEmptyUninitialised(dimensions);
        channel.voxels().asByte().slices().replaceSlice(0, VoxelBufferWrap.unsignedByteArray(arr));
        return channel;
    }

    private static byte[][] bytesFromBufferedImage(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        return bytesFromBufferedImage(raster, 0, 0, raster.getWidth(), raster.getHeight());
    }

    private static byte[][] bytesFromBufferedImage(
            WritableRaster raster, int x, int y, int sixeX, int sizeY) {

        if (x == 0
                && y == 0
                && sixeX == raster.getWidth()
                && sizeY == raster.getHeight()
                && raster.getDataBuffer() instanceof DataBufferByte) {
            return ((DataBufferByte) raster.getDataBuffer()).getBankData();
        }

        int bands = raster.getNumBands();
        byte[][] out = new byte[bands][sixeX * sizeY];
        int[] buf = new int[sixeX * sizeY];

        for (int i = 0; i < bands; i++) {
            raster.getSamples(x, y, sixeX, sizeY, i, buf);
            for (int j = 0; j < buf.length; j++) {
                out[i][j] = (byte) buf[j];
            }
        }
        return out;
    }
}
