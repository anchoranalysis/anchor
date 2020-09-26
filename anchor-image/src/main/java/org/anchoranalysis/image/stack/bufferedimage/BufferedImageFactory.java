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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BufferedImageFactory {

    public static BufferedImage create(Stack stack) throws CreateException {
        
        if (!stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE)) {
            throw new CreateException("This writer expects only 8-bit channels");
        }
        
        
        if (stack.getNumberChannels() == 3) {
            return BufferedImageFactory.createRGB(
                    extractVoxels(stack,0),
                    extractVoxels(stack,1),
                    extractVoxels(stack,2),
                    stack.extent());
        }
        
        if (stack.getNumberChannels()==1) {
            return BufferedImageFactory.createGrayscale( extractVoxels(stack,0) );
        }
        
        throw new CreateException("Only 1 or 3 channels are supported for conversion.");
    }
    
    public static BufferedImage createGrayscale(Voxels<UnsignedByteBuffer> voxels)
            throws CreateException {

        Extent e = voxels.extent();
        checkExtentZ(e);

        return createBufferedImageFromGrayscaleBuffer(voxels.sliceBuffer(0), e);
    }

    public static BufferedImage createRGB(
            Voxels<UnsignedByteBuffer> red,
            Voxels<UnsignedByteBuffer> green,
            Voxels<UnsignedByteBuffer> blue,
            Extent e)
            throws CreateException {
        checkExtentZ(e);

        BufferedImage bi = new BufferedImage(e.x(), e.y(), BufferedImage.TYPE_3BYTE_BGR);

        byte[] arrComb =
                createCombinedByteArray(
                        e,
                        firstBuffer(red, e, "red"),
                        firstBuffer(green, e, "green"),
                        firstBuffer(blue, e, "blue"));
        bi.getWritableTile(0, 0).setDataElements(0, 0, e.x(), e.y(), arrComb);

        return bi;
    }

    private static UnsignedByteBuffer firstBuffer(
            Voxels<UnsignedByteBuffer> voxels, Extent e, String dscr) throws CreateException {

        if (!voxels.extent().equals(e)) {
            throw new CreateException(dscr + " channel extent does not match");
        }

        return voxels.sliceBuffer(0);
    }

    private static BufferedImage createBufferedImageFromGrayscaleBuffer(
            UnsignedByteBuffer bufferGray, Extent extent) {

        BufferedImage image =
                new BufferedImage(extent.x(), extent.y(), BufferedImage.TYPE_BYTE_GRAY);

        byte[] arr = bufferGray.array();
        image.getWritableTile(0, 0).setDataElements(0, 0, extent.x(), extent.y(), arr);

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
        
    private static Voxels<UnsignedByteBuffer> extractVoxels(Stack stack, int channelIndex) {
        return stack.getChannel(channelIndex).voxels().asByte();
    }
}
