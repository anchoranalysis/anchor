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

package org.anchoranalysis.image.core.contour;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Finds contour voxels, the pixels forming a continuous path along the boundary of the object or
 * mask.
 *
 * <p>Specifically, it converts a solid-object (where all voxels inside an object are <i>on</i>)
 * into where only pixels on the contour are <i>on</i>.
 *
 * <p>A new object/mask is always created, so the existing buffers are not overwritten
 *
 * <p>The contour is always guaranteed to be inside the existing mask (so always a subset of
 * existing <i>on</i> voxels).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindContour {

    /**
     * Like {@link #createFrom(Mask, int, boolean, boolean)}, but guesses whether to do this in 2D
     * or 3D.
     *
     * <p>The guess depends on whether the mask has 3-dimensions or 2-dimensions.
     *
     * @param mask the mask to find a contour for.
     * @param numberErosions the number of erosions, effectively determining how thick the contour
     *     is.
     * @param force2D if true, 2D will <i>always</i> be used irrespective of the guessing.
     * @param atImageBoundary if true, contour voxels are shown also for the boundary of the scene.
     *     if false, this is not shown.
     * @return a newly-created mask showing only the contour.
     */
    public static Mask createFromGuess3D(
            Mask mask, int numberErosions, boolean force2D, boolean atImageBoundary) {
        boolean do2D = mask.dimensions().z() == 1 || force2D;
        return createFrom(mask, numberErosions, !do2D, atImageBoundary);
    }

    /**
     * Creates an contour from a {@link Mask} at the boundary of depth {@code 1} pixel.
     *
     * @param mask the mask.
     * @param numberErosions the number of erosions, effectively determining how thick the contour
     *     is.
     * @param do3D whether to also perform the contour in the third dimension. This is typically
     *     unwanted for 2 dimensional images, as every voxel inside the object is treated as on the
     *     boundary and a filled in object is produced.
     * @param atImageBoundary if true, contour voxels are shown also for the boundary of the scene.
     *     if false, this is not shown.
     * @return a newly-created mask showing only the contour.
     */
    public static Mask createFrom(
            Mask mask, int numberErosions, boolean do3D, boolean atImageBoundary) {
        // We create a new mask for outputting
        Mask maskOut = new Mask(mask.dimensions(), mask.binaryValuesInt());

        // Gets the contour
        contourMaskInto(mask, maskOut, numberErosions, do3D, atImageBoundary);

        return maskOut;
    }

    /**
     * Creates an contour from a {@link ObjectMask}.
     *
     * <p>The parameter {@code nubmerErosions} determines the depth of the boundary, with {@code 1}
     * giving a single-voxel depth.
     *
     * @param object object to find a contour for.
     * @param numberErosions the number of erosions, effectively determining how thick the contour
     *     is.
     * @param atImageBoundary if true, contour voxels are shown also for the boundary of the scene.
     *     if false, this is not shown.
     * @param do3D whether to also perform the contour in the third dimension. This is typically
     *     unwanted for 2 dimensional images, as every voxel inside the object is treated as on the
     *     boundary and a filled in object is produced.
     * @return a newly created {@link ObjectMask} describing the contour.
     */
    public static ObjectMask createFrom(
            ObjectMask object, int numberErosions, boolean do3D, boolean atImageBoundary) {

        Preconditions.checkArgument(numberErosions >= 1);

        ObjectMask objectDuplicated = object.duplicate();

        BinaryVoxels<UnsignedByteBuffer> voxelsOut =
                ContourFromBinaryVoxels.createFrom(
                        objectDuplicated.binaryVoxels(), numberErosions, do3D, atImageBoundary);
        return new ObjectMask(objectDuplicated.boundingBox(), voxelsOut);
    }

    /**
     * Creates a contour from a {@code toFindContourFor} mask, assigning the newly created voxels to
     * {@code toAssignContourTo}.
     *
     * <p>It assumes the two masks have the same binary-values.
     */
    private static void contourMaskInto(
            Mask toFindContourFor,
            Mask toAssignContourTo,
            int numberErosions,
            boolean do3D,
            boolean atImageBoundary) {

        BinaryVoxels<UnsignedByteBuffer> voxels =
                BinaryVoxelsFactory.reuseByte(
                        toFindContourFor.voxels(), toFindContourFor.binaryValuesInt());

        BinaryVoxels<UnsignedByteBuffer> contour =
                ContourFromBinaryVoxels.createFrom(voxels, numberErosions, do3D, atImageBoundary);

        try {
            toAssignContourTo.replaceBy(contour);
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
