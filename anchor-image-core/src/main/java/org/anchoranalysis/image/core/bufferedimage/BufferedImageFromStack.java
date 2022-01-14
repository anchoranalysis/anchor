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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.stack.RGBChannelNames;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.convert.bufferedimage.BufferedImageFromVoxels;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Converts a {@link Stack} or similar data-objects into an AWT {@link BufferedImage}.
 *
 * <p>Please see {@link BufferedImageFromVoxels} to convert from a {@link Voxels} instance.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BufferedImageFromStack {

    /**
     * Creates a {@link BufferedImage} from a {@link Stack}.
     *
     * <p>The following situations are supported with the {@link Stack}:
     *
     * <ul>
     *   <li>Three channels of type {@link UnsignedByteVoxelType}, representing the colors red,
     *       green, blue respectively.
     *   <li>A single-channel of type {@link UnsignedByteVoxelType}.
     *   <li>A single-channel of type {@link UnsignedShortVoxelType}.
     * </ul>
     *
     * @param stack the stack.
     * @return a newly created {@link BufferedImage}.
     * @throws CreateException if the stack does not conform to a supported data-type or number of
     *     channels <i>or</i> if the stack is 3D which is unsupported.
     */
    public static BufferedImage create(Stack stack) throws CreateException {

        if (stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE)) {
            if (stack.getNumberChannels() == 3) {
                return createRGB(
                        voxelsAsByte(stack, 0),
                        voxelsAsByte(stack, 1),
                        voxelsAsByte(stack, 2),
                        stack.extent());
            }

            if (stack.getNumberChannels() == 1) {
                return BufferedImageFromVoxels.createGrayscaleByte(voxelsAsByte(stack, 0));
            }

            throw new CreateException(
                    "Only single or three- channeled are supported for unsigned 8-bit conversion.");
        } else if (stack.allChannelsHaveType(UnsignedShortVoxelType.INSTANCE)) {

            if (stack.getNumberChannels() == 1) {
                return BufferedImageFromVoxels.createGrayscaleShort(voxelsAsShort(stack, 0));
            }

            throw new CreateException(
                    "Only single-channeled images are supported for unsigned 16-bit conversion.");
        } else {
            throw new CreateException(
                    "Only single or three-channeled unsigned 8-bit images or single-chanelled unsigned 16-bit images are supported.");
        }
    }

    /**
     * Creates a {@link BufferedImage} from three {@code Voxels<UnsignedByteBuffer>} representing
     * respectively, red, green, blue color components.
     *
     * <p>All three {@link Voxels} must be sized exactly as {@code extent}.
     *
     * @param red the voxels describing the <i>red</i> color component.
     * @param green the voxels describing the <i>green</i> color component.
     * @param blue the voxels describing the <i>blue</i> color component.
     * @param extent the size of the {@link BufferedImage} to create.
     * @return a newly created 8-bit {@link BufferedImage} that reuses the underlying array in the
     *     buffer of {@code voxels}.
     * @throws CreateException if the stack does not conform to a supported data-type or number of
     *     channels <i>or</i> if the stack is 3D which is unsupported.
     */
    public static BufferedImage createRGB(
            Voxels<UnsignedByteBuffer> red,
            Voxels<UnsignedByteBuffer> green,
            Voxels<UnsignedByteBuffer> blue,
            Extent extent)
            throws CreateException {
        checkExtentZ(extent);

        // TODO is it right that this refers to BGR but we input the channels in RGB order?
        BufferedImage image =
                new BufferedImage(extent.x(), extent.y(), BufferedImage.TYPE_3BYTE_BGR);

        byte[] combined =
                deriveInterleavedArray(
                        extent,
                        firstBuffer(red, extent, RGBChannelNames.RED),
                        firstBuffer(green, extent, RGBChannelNames.GREEN),
                        firstBuffer(blue, extent, RGBChannelNames.BLUE));
        image.getWritableTile(0, 0).setDataElements(0, 0, extent.x(), extent.y(), combined);

        return image;
    }

    /** The buffer corresponding to the first z-slice. */
    private static UnsignedByteBuffer firstBuffer(
            Voxels<UnsignedByteBuffer> voxels, Extent extent, String dscr) throws CreateException {

        if (!voxels.extent().equals(extent)) {
            throw new CreateException(dscr + " channel extent does not match");
        }

        return voxels.sliceBuffer(0);
    }

    /**
     * Creates a new {@code byte[]} array with successive voxels referring to red, green, blue
     * voxels respectively.
     *
     * <p>These voxels are considered <a
     * href="http://casu.ast.cam.ac.uk/surveys-projects/wfcam/technical/interleaving">interleaved</a>.
     *
     * <p>Each buffer should have the same length as the volume of {@code extent}.
     *
     * @param extent the size of the image the buffers refer to.
     * @param red a buffer containing only red voxels, in the same order as {@code green} and {@code
     *     blue}.
     * @param green a buffer containing only red voxels, in the same order as {@code red} and {@code
     *     blue}.
     * @param blue a buffer containing only red voxels, in the same order as {@code red} and {@code
     *     green}.
     * @return a newly created array that is three-times the size of any individual buffer, with
     *     interleaved voxels as above.
     */
    private static byte[] deriveInterleavedArray(
            Extent extent,
            UnsignedByteBuffer red,
            UnsignedByteBuffer green,
            UnsignedByteBuffer blue) {

        int size = extent.calculateVolumeAsInt();
        byte[] combined = new byte[size * 3];
        int count = 0;
        for (int i = 0; i < size; i++) {

            combined[count++] = red.getRaw(i);
            combined[count++] = green.getRaw(i);
            combined[count++] = blue.getRaw(i);
        }
        return combined;
    }

    /** Throws an exception if {@code extent} describes a 3D image, and does nothing of it is 2D. */
    private static void checkExtentZ(Extent extent) throws CreateException {
        if (extent.z() != 1) {
            throw new CreateException("The Z-dimension must be 1.");
        }
    }

    /** Extracts a particular channel as {@link Voxels} of type <b>unsigned byte</b>. */
    private static Voxels<UnsignedByteBuffer> voxelsAsByte(Stack stack, int channelIndex) {
        return stack.getChannel(channelIndex).voxels().asByte();
    }

    /** Extracts a particular channel as {@link Voxels} of type <b>unsigned unshort</b>. */
    private static Voxels<UnsignedShortBuffer> voxelsAsShort(Stack stack, int channelIndex) {
        return stack.getChannel(channelIndex).voxels().asShort();
    }
}
