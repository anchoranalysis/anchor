/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.convert.bufferedimage;

import java.awt.image.BufferedImage;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Converts a {@link Voxels} instance into an AWT {@link BufferedImage}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BufferedImageFromVoxels {

    /**
     * Creates a {@link BufferedImage} from a {@code Voxels<UnsignedByteBuffer>}.
     *
     * @param voxels the voxels.
     * @return a newly created 8-bit {@link BufferedImage} that reuses the underlying array in the
     *     buffer of {@code voxels}.
     * @throws CreateException if the stack does not conform to a supported data-type or number of
     *     channels <i>or</i> if the stack is 3D which is unsupported.
     */
    public static BufferedImage createGrayscaleByte(Voxels<UnsignedByteBuffer> voxels)
            throws CreateException {
        return createFromArray(voxels, BufferedImage.TYPE_BYTE_GRAY, UnsignedByteBuffer::array);
    }

    /**
     * Creates a {@link BufferedImage} from a {@code Voxels<UnsignedShortBuffer>}.
     *
     * @param voxels the voxels.
     * @return a newly created 16-bit {@link BufferedImage} that reuses the underlying array in the
     *     buffer of {@code voxels}.
     * @throws CreateException if the stack does not conform to a supported data-type or number of
     *     channels <i>or</i> if the stack is 3D which is unsupported.
     */
    public static BufferedImage createGrayscaleShort(Voxels<UnsignedShortBuffer> voxels)
            throws CreateException {
        return createFromArray(voxels, BufferedImage.TYPE_USHORT_GRAY, UnsignedShortBuffer::array);
    }

    /**
     * Creates a {@link BufferedImage} from {@link Voxels}.
     *
     * @param <T> buffer-type for voxels {@link Voxels} as used in Anchor.
     * @param <S> Java primitive type representing an array of primitives corresponding to {@code
     *     imageType}, representing each voxel.
     * @param voxels the voxels to convert into a {@link BufferedImage}.
     * @param imageType the voxel data-type as per the final argument in {@link
     *     BufferedImage#BufferedImage(int, int, int)}.
     * @param arrayFromBuffer extracts an array of type {@code S} from a buffer of type {@code T}.
     * @return a newly created {@link BufferedImage} that <i>reuses</i> {@code voxelArray}
     *     internally.
     * @throws CreateException if {@code voxels} is 3D which is unsupported (i.e. has more than one
     *     z-slice).
     */
    private static <T, S> BufferedImage createFromArray(
            Voxels<T> voxels, int imageType, Function<T, S> arrayFromBuffer)
            throws CreateException {

        Extent extent = voxels.extent();
        checkExtentZ(extent);

        return createGrayscaleFromArray(
                arrayFromBuffer.apply(voxels.sliceBuffer(0)), extent, imageType);
    }

    /**
     * Creates a {@link BufferedImage} from an array of voxels, representing a single-channel.
     *
     * @param <S> type representing an array of primitives corresponding to {@code imageType},
     *     representing each voxel.
     * @param voxelArray the array of primitives, representing each voxel, with exactly as many
     *     elements as the volume of {@code extent}.
     * @param extent the size of the image.
     * @param imageType the voxel data-type as per the final argument in {@link
     *     BufferedImage#BufferedImage(int, int, int)}.
     * @return a newly created {@link BufferedImage} that <i>reuses</i> {@code voxelArray}
     *     internally.
     */
    private static <S> BufferedImage createGrayscaleFromArray(
            S voxelArray, Extent extent, int imageType) {

        BufferedImage image = new BufferedImage(extent.x(), extent.y(), imageType);
        image.getWritableTile(0, 0).setDataElements(0, 0, extent.x(), extent.y(), voxelArray);
        return image;
    }

    /** Throws an exception if {@code extent} describes a 3D image, and does nothing of it is 2D. */
    private static void checkExtentZ(Extent extent) throws CreateException {
        if (extent.z() != 1) {
            throw new CreateException("The Z-dimension must be 1.");
        }
    }
}
