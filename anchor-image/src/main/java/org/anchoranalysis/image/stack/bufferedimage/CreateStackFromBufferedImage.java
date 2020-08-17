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

package org.anchoranalysis.image.stack.bufferedimage;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.stream.IntStream;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CreateStackFromBufferedImage {

    private static final ChannelFactorySingleType FACTORY = new ChannelFactoryByte();

    public static Stack create(BufferedImage bufferedImage) throws OperationFailedException {

        ImageDimensions dimensions =
                new ImageDimensions(bufferedImage.getWidth(), bufferedImage.getHeight(), 1);

        byte[][] arr = bytesFromBufferedImage(bufferedImage);

        try {
            return new Stack(
                IntStream.range(0, arr.length).mapToObj( channelIndex->
                    createChannelFor(dimensions, arr[channelIndex])
                )
            );
            
        } catch (IncorrectImageSizeException e) {
            throw new OperationFailedException(e);
        }
    }
    
    private static Channel createChannelFor(ImageDimensions dimensions, byte[] arr) {
        Channel channel = FACTORY.createEmptyUninitialised(dimensions);
        channel.voxels()
                .asByte()
                .slices()
                .replaceSlice(0, VoxelBufferByte.wrap(arr));
        return channel;
    }

    private static byte[][] bytesFromBufferedImage(BufferedImage image) {
        WritableRaster wr = image.getRaster();
        return bytesFromBufferedImage(wr, 0, 0, wr.getWidth(), wr.getHeight());
    }

    private static byte[][] bytesFromBufferedImage(
            WritableRaster wr, int x, int y, int sx, int sy) {

        if (x == 0
                && y == 0
                && sx == wr.getWidth()
                && sy == wr.getHeight()
                && wr.getDataBuffer() instanceof DataBufferByte) {
            return ((DataBufferByte) wr.getDataBuffer()).getBankData();
        }

        int bands = wr.getNumBands();
        byte[][] out = new byte[bands][sx * sy];
        int[] buf = new int[sx * sy];

        for (int i = 0; i < bands; i++) {
            wr.getSamples(x, y, sx, sy, i, buf);
            for (int j = 0; j < buf.length; j++) {
                out[i][j] = (byte) buf[j];
            }
        }
        return out;
    }
}
