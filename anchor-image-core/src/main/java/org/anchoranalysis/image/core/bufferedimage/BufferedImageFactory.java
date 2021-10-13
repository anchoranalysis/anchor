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
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.stack.RGBChannelNames;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.spatial.box.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BufferedImageFactory {

    public static BufferedImage create(Stack stack) throws CreateException {

        if (stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE)) {
            if (stack.getNumberChannels() == 3) {
                return BufferedImageFactory.createRGB(
                        extractVoxelsAsByte(stack, 0),
                        extractVoxelsAsByte(stack, 1),
                        extractVoxelsAsByte(stack, 2),
                        stack.extent());
            }

            if (stack.getNumberChannels() == 1) {
                return BufferedImageFactory.createGrayscaleByte(extractVoxelsAsByte(stack, 0));
            }

            throw new CreateException(
                    "Only single or three- channeled are supported for unsigned 8-bit conversion.");
        } else if (stack.allChannelsHaveType(UnsignedShortVoxelType.INSTANCE)) {

            if (stack.getNumberChannels() == 1) {
                return BufferedImageFactory.createGrayscaleShort(extractVoxelsAsShort(stack, 0));
            }

            throw new CreateException(
                    "Only single-channeled images are supported for unsigned 16-bit conversion.");
        } else {
            throw new CreateException(
                    "Only single or three-channeled unsigned 8-bit images or single-chanelled unsigned 16-bit images are supported.");
        }
    }

    public static BufferedImage createGrayscaleByte(Voxels<UnsignedByteBuffer> voxels)
            throws CreateException {
        return createGrayscale(voxels, BufferedImage.TYPE_BYTE_GRAY, UnsignedByteBuffer::array);
    }

    private static BufferedImage createGrayscaleShort(Voxels<UnsignedShortBuffer> voxels)
            throws CreateException {
        return createGrayscale(voxels, BufferedImage.TYPE_USHORT_GRAY, UnsignedShortBuffer::array);
    }

    private static <T> BufferedImage createGrayscale(
            Voxels<T> voxels, int pixelType, Function<T, Object> extractArray)
            throws CreateException {

        Extent extent = voxels.extent();
        checkExtentZ(extent);

        return createBufferedImageFromGrayscaleBuffer(
                extractArray.apply(voxels.sliceBuffer(0)), extent, pixelType);
    }

    public static BufferedImage createRGB(
            Voxels<UnsignedByteBuffer> red,
            Voxels<UnsignedByteBuffer> green,
            Voxels<UnsignedByteBuffer> blue,
            Extent e)
            throws CreateException {
        checkExtentZ(e);

        BufferedImage image = new BufferedImage(e.x(), e.y(), BufferedImage.TYPE_3BYTE_BGR);

        byte[] combined =
                createCombinedByteArray(
                        e,
                        firstBuffer(red, e, RGBChannelNames.RED),
                        firstBuffer(green, e, RGBChannelNames.GREEN),
                        firstBuffer(blue, e, RGBChannelNames.BLUE));
        image.getWritableTile(0, 0).setDataElements(0, 0, e.x(), e.y(), combined);

        return image;
    }

    private static UnsignedByteBuffer firstBuffer(
            Voxels<UnsignedByteBuffer> voxels, Extent e, String dscr) throws CreateException {

        if (!voxels.extent().equals(e)) {
            throw new CreateException(dscr + " channel extent does not match");
        }

        return voxels.sliceBuffer(0);
    }

    private static BufferedImage createBufferedImageFromGrayscaleBuffer(
            Object voxels, Extent extent, int imageType) {

        BufferedImage image = new BufferedImage(extent.x(), extent.y(), imageType);
        image.getWritableTile(0, 0).setDataElements(0, 0, extent.x(), extent.y(), voxels);
        return image;
    }

    private static byte[] createCombinedByteArray(
            Extent e,
            UnsignedByteBuffer bufferRed,
            UnsignedByteBuffer bufferGreen,
            UnsignedByteBuffer bufferBlue) {

        int size = e.calculateVolumeAsInt();
        byte[] combined = new byte[size * 3];
        int count = 0;
        for (int i = 0; i < size; i++) {

            combined[count++] = bufferRed.getRaw(i);
            combined[count++] = bufferGreen.getRaw(i);
            combined[count++] = bufferBlue.getRaw(i);
        }
        return combined;
    }

    private static void checkExtentZ(Extent e) throws CreateException {
        if (e.z() != 1) {
            throw new CreateException("z dimension must be 1");
        }
    }

    private static Voxels<UnsignedByteBuffer> extractVoxelsAsByte(Stack stack, int channelIndex) {
        return stack.getChannel(channelIndex).voxels().asByte();
    }

    private static Voxels<UnsignedShortBuffer> extractVoxelsAsShort(Stack stack, int channelIndex) {
        return stack.getChannel(channelIndex).voxels().asShort();
    }
}
