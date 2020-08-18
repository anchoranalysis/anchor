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

package org.anchoranalysis.image.outline;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.mask.combine.MaskXor;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelsFactory;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.dilateerode.ErosionKernel3;
import org.anchoranalysis.image.voxel.kernel.outline.OutlineKernel3;

/**
 * Finds outline voxels i.e. pixels on the contour/edge of the object
 *
 * <p>Specifically, it converts a solid-object (where all voxels inside an object are ON) into where
 * only pixels on the contour are ON
 *
 * <p>A new object/mask is always created, so the existing buffers are not overwritten
 *
 * <p>The outline is always guaranteed to be inside the existing mask (so always a subset of ON
 * voxels).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindOutline {

    /**
     * Finds the outline of a mask, guessing whether to do this in 2D or 3D depending on if the mask
     * has 3-dimensions
     *
     * @param mask the mask to find an outline for
     * @param force2D if TRUE, 2D will ALWAYS be used irrespective of the guessing
     * @param outlineAtBoundary if true, an edge is shown also for the boundary of the scene. if
     *     false, this is not shown.
     * @return a newly-created mask showing only the outline
     */
    public static Mask outlineGuess3D(Mask mask, boolean force2D, boolean outlineAtBoundary) {
        boolean do2D = mask.dimensions().z() == 1 || force2D;
        return outline(mask, !do2D, outlineAtBoundary);
    }

    public static Mask outline(Mask mask, boolean do3D, boolean erodeAtBoundary) {
        // We create a new mask for outputting
        Mask maskOut = new Mask(mask.dimensions(), mask.binaryValues());

        // Gets outline
        outlineMaskInto(mask, maskOut, do3D, erodeAtBoundary);

        return maskOut;
    }

    /**
     * Creates outline from an object
     *
     * <p>It potentially uses multiple erosions to create a deeper outline.
     *
     * @param object object to find outline for
     * @param numberErosions the number of erosions, effectively determining how thick the outline
     *     is
     * @param outlineAtBoundary if true, an edge is shown also for the boundary of the scene. if
     *     false, this is not shown.
     * @param do3D whether to also perform the outline in the third dimension. This is a bad idea
     *     for 2 dimensional images, as every voxel inside the object is treated as on the boundary
     *     and a filled in object is produced.
     */
    public static ObjectMask outline(
            ObjectMask object, int numberErosions, boolean outlineAtBoundary, boolean do3D) {

        ObjectMask objectDuplicated = object.duplicate();

        if (numberErosions < 1) {
            assert false;
        }

        BinaryVoxels<ByteBuffer> voxelsOut =
                outlineMultiplex(
                        objectDuplicated.binaryVoxels(), numberErosions, outlineAtBoundary, do3D);
        return new ObjectMask(
                objectDuplicated.boundingBox(), voxelsOut.voxels(), voxelsOut.binaryValues());
    }

    private static BinaryVoxels<ByteBuffer> outlineMultiplex(
            BinaryVoxels<ByteBuffer> voxels,
            int numberErosions,
            boolean erodeAtBoundary,
            boolean do3D) {
        // If we just want an edge of size 1, we can do things more optimally
        if (numberErosions == 1) {
            return outlineByKernel(voxels, erodeAtBoundary, do3D);
        } else {
            return outlineByErosion(voxels, numberErosions, erodeAtBoundary, do3D);
        }
    }

    // Assumes imgChnlOut has the same ImgChnlRegions
    private static void outlineMaskInto(
            Mask maskToFindOutlineFor,
            Mask maskToReplaceWithOutline,
            boolean do3D,
            boolean erodeAtBoundary) {

        BinaryVoxels<ByteBuffer> voxels =
                BinaryVoxelsFactory.reuseByte(
                        maskToFindOutlineFor.voxels(), maskToFindOutlineFor.binaryValues());

        BinaryVoxels<ByteBuffer> outline = outlineByKernel(voxels, erodeAtBoundary, do3D);

        try {
            maskToReplaceWithOutline.replaceBy(outline);
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /** Find an outline only 1 pixel deep by using a kernel directly */
    private static BinaryVoxels<ByteBuffer> outlineByKernel(
            BinaryVoxels<ByteBuffer> voxels, boolean erodeAtBoundary, boolean do3D) {

        // if our solid is too small, we don't apply the kernel, as it fails on anything less than
        // 3x3, and instead we simply return the solid as it is
        if (isTooSmall(voxels.extent(), do3D)) {
            return voxels.duplicate();
        }

        BinaryValuesByte bvb = voxels.binaryValues().createByte();

        BinaryKernel kernel = new OutlineKernel3(bvb, !erodeAtBoundary, do3D);

        Voxels<ByteBuffer> out = ApplyKernel.apply(kernel, voxels.voxels(), bvb);
        return BinaryVoxelsFactory.reuseByte(out, voxels.binaryValues());
    }

    /**
     * Find an outline by doing (maybe more than 1) morphological erosions, and subtracting from
     * original object
     */
    private static BinaryVoxels<ByteBuffer> outlineByErosion(
            BinaryVoxels<ByteBuffer> voxels,
            int numberErosions,
            boolean outlineAtBoundary,
            boolean do3D) {

        // Otherwise if > 1
        Voxels<ByteBuffer> eroded = multipleErode(voxels, numberErosions, outlineAtBoundary, do3D);

        // Binary and between the original version and the eroded version
        assert (eroded != null);
        BinaryValuesByte bvb = voxels.binaryValues().createByte();
        MaskXor.apply(voxels.voxels(), eroded, bvb, bvb);
        return voxels;
    }

    private static Voxels<ByteBuffer> multipleErode(
            BinaryVoxels<ByteBuffer> voxels,
            int numberErosions,
            boolean erodeAtBoundary,
            boolean do3D) {

        BinaryValuesByte bvb = voxels.binaryValues().createByte();
        BinaryKernel kernelErosion = new ErosionKernel3(bvb, erodeAtBoundary, do3D);

        Voxels<ByteBuffer> eroded = ApplyKernel.apply(kernelErosion, voxels.voxels(), bvb);
        for (int i = 1; i < numberErosions; i++) {
            eroded = ApplyKernel.apply(kernelErosion, eroded, bvb);
        }
        return eroded;
    }

    private static boolean isTooSmall(Extent e, boolean do3D) {
        if (e.x() < 3 || e.y() < 3) {
            return true;
        }
        return (do3D && e.z() < 3);
    }
}
