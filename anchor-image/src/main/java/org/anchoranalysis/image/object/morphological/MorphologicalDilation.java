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

package org.anchoranalysis.image.object.morphological;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelsFactory;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.morphological.accept.AcceptIterationConditon;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.kernel.ApplyKernel;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.ConditionalKernel;
import org.anchoranalysis.image.voxel.kernel.dilateerode.DilationKernel3;
import org.anchoranalysis.image.voxel.kernel.dilateerode.DilationKernel3ZOnly;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MorphologicalDilation {

    /**
     * Dilates an object-mask, growing the bounding-box as necessary.
     *
     * @param object the object to dilate
     * @param extent if present, restricts the obejct to remain within certain bounds
     * @param do3D whether to perform dilation in 3D or 2D
     * @param iterations number of dilations to perform
     * @return a newly created object-mask with bounding-box grown in relevant directions by {@code
     *     iterations}
     * @throws CreateException
     */
    public static ObjectMask createDilatedObject(
            ObjectMask object,
            Optional<Extent> extent,
            boolean do3D,
            int iterations,
            boolean bigNeighborhood)
            throws CreateException {

        Point3i grow =
                do3D
                        ? new Point3i(iterations, iterations, iterations)
                        : new Point3i(iterations, iterations, 0);

        try {
            ObjectMask objectGrown = object.growBuffer(grow, grow, extent);
            return objectGrown.replaceVoxels(
                    dilate(objectGrown.binaryVoxels(), do3D, iterations, null, 0, bigNeighborhood)
                            .voxels());
        } catch (OperationFailedException e) {
            throw new CreateException("Cannot grow object-mask", e);
        }
    }

    public static BinaryVoxels<UnsignedByteBuffer> dilate(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            boolean do3D,
            int iterations,
            Optional<Voxels<UnsignedByteBuffer>> backgroundVb,
            int minIntensityValue,
            boolean bigNeighborhood)
            throws CreateException {
        return dilate(
                voxels,
                SelectDimensionsFactory.of(do3D),
                iterations,
                backgroundVb,
                minIntensityValue,
                false,
                Optional.empty(),
                bigNeighborhood);
    }

    /**
     * Performs a morpholgical dilation operation
     *
     * @param voxels input-voxels
     * @param dimensions selects which dimensions dilation is applied on
     * @param iterations number of dilations
     * @param background optional background-buffer that can influence the dilation with the
     *     minIntensityValue
     * @param minIntensityValue minimumIntensity on the background, for a pixel to be included
     * @param outsideAtThreshold if true, pixels outside the buffer are treated as ON, otherwise as
     *     OFF
     * @param acceptConditions if non-null, imposes a condition on each iteration that must be
     *     passed
     * @return a new buffer containing the results of the dilation-operations
     * @throws CreateException
     */
    public static BinaryVoxels<UnsignedByteBuffer> dilate(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            SelectDimensions dimensions,
            int iterations,
            Optional<Voxels<UnsignedByteBuffer>> background,
            int minIntensityValue,
            boolean outsideAtThreshold,
            Optional<AcceptIterationConditon> acceptConditions,
            boolean bigNeighborhood)
            throws CreateException {

        BinaryKernel kernelDilation =
                createDilationKernel(
                        voxels.binaryValues().createByte(),
                        dimensions,
                        background,
                        minIntensityValue,
                        outsideAtThreshold,
                        bigNeighborhood);

        Voxels<UnsignedByteBuffer> buf = voxels.voxels();
        for (int i = 0; i < iterations; i++) {
            Voxels<UnsignedByteBuffer> next =
                    ApplyKernel.apply(kernelDilation, buf, voxels.binaryValues().createByte());

            try {
                if (acceptConditions.isPresent()
                        && !acceptConditions.get().acceptIteration(next, voxels.binaryValues())) {
                    break;
                }
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }

            buf = next;
        }
        return BinaryVoxelsFactory.reuseByte(buf, voxels.binaryValues());
    }

    private static BinaryKernel createDilationKernel(
            BinaryValuesByte binaryValues,
            SelectDimensions dimensions,
            Optional<Voxels<UnsignedByteBuffer>> backgroundVb,
            int minIntensityValue,
            boolean outsideAtThreshold,
            boolean bigNeighborhood)
            throws CreateException {

        BinaryKernel kernelDilation =
                createDilationKernel(binaryValues, dimensions, outsideAtThreshold, bigNeighborhood);

        if (minIntensityValue > 0 && backgroundVb.isPresent()) {
            return new ConditionalKernel(kernelDilation, minIntensityValue, backgroundVb.get());
        } else {
            return kernelDilation;
        }
    }

    private static BinaryKernel createDilationKernel(
            BinaryValuesByte binaryValues,
            SelectDimensions dimensions,
            boolean outsideAtThreshold,
            boolean bigNeighborhood)
            throws CreateException {
        if (dimensions == SelectDimensions.Z_ONLY) {
            if (bigNeighborhood) {
                throw new CreateException("Big-neighborhood not supported for zOnly");
            }

            return new DilationKernel3ZOnly(binaryValues, outsideAtThreshold);
        } else {
            return new DilationKernel3(
                    binaryValues,
                    outsideAtThreshold,
                    dimensions == SelectDimensions.ALL_DIMENSIONS,
                    bigNeighborhood);
        }
    }
}
