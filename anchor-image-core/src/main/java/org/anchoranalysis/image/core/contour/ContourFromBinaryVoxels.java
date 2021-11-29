/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.contour;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.mask.combine.MaskXor;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.kernel.morphological.ErosionKernel;
import org.anchoranalysis.image.voxel.kernel.outline.OutlineKernel;
import org.anchoranalysis.spatial.box.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ContourFromBinaryVoxels {

    /**
     * Finds a contour, selecting between two methods, based upon {@code numberErosions}.
     *
     * @param voxels the voxels to find a contour for.
     * @param numberErosions the number of erosions, effectively determining how thick the contour
     *     is.
     * @param do3D whether to also perform the contour in the third dimension. This is typically
     *     unwanted for 2 dimensional images, as every voxel inside the object is treated as on the
     *     boundary and a filled in object is produced.
     * @param atImageBoundary if true, contour voxels are shown also for the boundary of the scene.
     *     if false, this is not shown.
     * @return newly created {@link BinaryVoxels} indicating the contour voxels.
     */
    public static BinaryVoxels<UnsignedByteBuffer> createFrom(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            int numberErosions,
            boolean do3D,
            boolean atImageBoundary) {
        // If we just want an edge of size 1, we can do things more optimally
        if (numberErosions == 1) {
            return contourByKernel(voxels, atImageBoundary, do3D);
        } else {
            return contourByErosion(voxels, numberErosions, atImageBoundary, do3D);
        }
    }

    /** Find a contour only 1 pixel deep by using a kernel directly. */
    private static BinaryVoxels<UnsignedByteBuffer> contourByKernel(
            BinaryVoxels<UnsignedByteBuffer> voxels, boolean atImageBoundary, boolean do3D) {

        // if our solid is too small, we don't apply the kernel, as it fails on anything less than
        // 3x3, and instead we simply return the solid as it is
        if (isTooSmall(voxels.extent(), do3D)) {
            return voxels.duplicate();
        }

        BinaryKernel kernel = new OutlineKernel();

        return ApplyKernel.apply(
                kernel,
                voxels,
                new KernelApplicationParameters(OutsideKernelPolicy.as(!atImageBoundary), do3D));
    }

    /**
     * Find an contour by doing (maybe more than 1) morphological erosions, and subtracting from
     * original object.
     */
    private static BinaryVoxels<UnsignedByteBuffer> contourByErosion(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            int numberErosions,
            boolean atImageBoundary,
            boolean do3D) {

        KernelApplicationParameters params =
                new KernelApplicationParameters(OutsideKernelPolicy.as(atImageBoundary), do3D);

        // Otherwise if > 1
        BinaryVoxels<UnsignedByteBuffer> eroded = multipleErode(voxels, numberErosions, params);

        // Binary and between the original version and the eroded version
        MaskXor.apply(voxels, eroded);
        return voxels;
    }

    private static BinaryVoxels<UnsignedByteBuffer> multipleErode(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            int numberErosions,
            KernelApplicationParameters params) {

        BinaryKernel kernelErosion = new ErosionKernel();

        BinaryVoxels<UnsignedByteBuffer> eroded = ApplyKernel.apply(kernelErosion, voxels, params);
        for (int i = 1; i < numberErosions; i++) {
            eroded = ApplyKernel.apply(kernelErosion, eroded, params);
        }
        return eroded;
    }

    private static boolean isTooSmall(Extent extent, boolean do3D) {
        if (extent.x() < 3 || extent.y() < 3) {
            return true;
        }
        return (do3D && extent.z() < 3);
    }
}
