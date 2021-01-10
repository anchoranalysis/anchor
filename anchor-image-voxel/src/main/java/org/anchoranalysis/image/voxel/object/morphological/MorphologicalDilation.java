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

package org.anchoranalysis.image.voxel.object.morphological;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.kernel.morphological.DilationKernelFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.morphological.predicate.AcceptIterationPredicate;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MorphologicalDilation {

    /**
     * Dilates an object-mask, growing the bounding-box as necessary.
     *
     * @param object the object to dilate
     * @param extent if present, restricts the obejct to remain within certain bounds
     * @param useZ whether to perform dilation in 3D or 2D
     * @param iterations number of dilations to perform
     * @return a newly created object-mask with bounding-box grown in relevant directions by {@code
     *     iterations}
     * @throws CreateException
     */
    public static ObjectMask createDilatedObject(
            ObjectMask object,
            Optional<Extent> extent,
            boolean useZ,
            int iterations,
            boolean bigNeighborhood)
            throws CreateException {

        Point3i grow =
                useZ
                        ? new Point3i(iterations, iterations, iterations)
                        : new Point3i(iterations, iterations, 0);

        try {
            KernelApplicationParameters parameters = new KernelApplicationParameters(OutsideKernelPolicy.IGNORE_OUTSIDE, useZ);
            ObjectMask objectGrown = object.growBuffer(grow, grow, extent);
            return objectGrown.replaceVoxels(
                    dilate(objectGrown.binaryVoxels(), parameters, iterations, null, 0, bigNeighborhood)
                            .voxels());
        } catch (OperationFailedException e) {
            throw new CreateException("Cannot grow object-mask", e);
        }
    }

    public static BinaryVoxels<UnsignedByteBuffer> dilate(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters parameters,
            int iterations,
            Optional<Voxels<UnsignedByteBuffer>> backgroundVb,
            int minIntensityValue,
            boolean bigNeighborhood)
            throws CreateException {
        return dilate(
                voxels,
                iterations,
                backgroundVb,
                minIntensityValue,
                Optional.empty(),
                new DilationKernelFactory(parameters, bigNeighborhood));
    }

    /**
     * Performs a morpholgical dilation operation
     *
     * @param voxelsBinary input-voxels
     * @param iterations number of dilations
     * @param background optional background-buffer that can influence the dilation with the
     *     minIntensityValue
     * @param minIntensityValue minimumIntensity on the background, for a pixel to be included
     * @param acceptConditions if non-null, imposes a condition on each iteration that must be
     *     passed
     * @return a new buffer containing the results of the dilation-operations
     * @throws CreateException
     */
    public static BinaryVoxels<UnsignedByteBuffer> dilate(
            BinaryVoxels<UnsignedByteBuffer> voxelsBinary,
            int iterations,
            Optional<Voxels<UnsignedByteBuffer>> background,
            int minIntensityValue,
            Optional<AcceptIterationPredicate> acceptConditions,
            DilationKernelFactory dilationKernelFactory)
            throws CreateException {

        BinaryKernel kernelDilation =
                dilationKernelFactory.createDilationKernel(background, minIntensityValue);

        KernelApplicationParameters params = dilationKernelFactory.createParameters();

        for (int i = 0; i < iterations; i++) {
            BinaryVoxels<UnsignedByteBuffer> next =
                    ApplyKernel.apply(kernelDilation, voxelsBinary, params);

            try {
                if (acceptConditions.isPresent() && !acceptConditions.get().acceptIteration(next)) {
                    break;
                }
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }

            voxelsBinary = next;
        }
        return voxelsBinary;
    }
}
