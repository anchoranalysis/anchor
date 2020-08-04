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

package org.anchoranalysis.image.object.morph;

import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelsFactory;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.morph.accept.AcceptIterationConditon;
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
                            .getVoxels());
        } catch (OperationFailedException e) {
            throw new CreateException("Cannot grow object-mask", e);
        }
    }

    public static BinaryVoxels<ByteBuffer> dilate(
            BinaryVoxels<ByteBuffer> bvb,
            boolean do3D,
            int iterations,
            Optional<Voxels<ByteBuffer>> backgroundVb,
            int minIntensityValue,
            boolean bigNeighborhood)
            throws CreateException {
        return dilate(
                bvb,
                do3D,
                iterations,
                backgroundVb,
                minIntensityValue,
                false,
                false,
                Optional.empty(),
                bigNeighborhood);
    }

    /**
     * Performs a morpholgical dilation operation
     *
     * <p>TODO: merge do3D and zOnly parameters into an enum
     *
     * @param bvb input-buffer
     * @param do3D if TRUE, 6-neighborhood dilation, otherwise 4-neighnrouhood
     * @param iterations number of dilations
     * @param backgroundVb optional background-buffer that can influence the dilation with the
     *     minIntensityValue
     * @param minIntensityValue minimumIntensity on the background, for a pixel to be included
     * @param zOnly if TRUE, only peforms dilation in z direction. Requires do3D to be TRUE
     * @param outsideAtThreshold if TRUE, pixels outside the buffer are treated as ON, otherwise as
     *     OFF
     * @param acceptConditions if non-NULL, imposes a condition on each iteration that must be
     *     passed
     * @return a new buffer containing the results of the dilation-operations
     * @throws CreateException
     */
    public static BinaryVoxels<ByteBuffer> dilate(
            BinaryVoxels<ByteBuffer> bvb,
            boolean do3D,
            int iterations,
            Optional<Voxels<ByteBuffer>> backgroundVb,
            int minIntensityValue,
            boolean zOnly,
            boolean outsideAtThreshold,
            Optional<AcceptIterationConditon> acceptConditions,
            boolean bigNeighborhood)
            throws CreateException {

        BinaryKernel kernelDilation =
                createDilationKernel(
                        bvb.getBinaryValues().createByte(),
                        do3D,
                        backgroundVb,
                        minIntensityValue,
                        zOnly,
                        outsideAtThreshold,
                        bigNeighborhood);

        Voxels<ByteBuffer> buf = bvb.getVoxels();
        for (int i = 0; i < iterations; i++) {
            Voxels<ByteBuffer> next =
                    ApplyKernel.apply(kernelDilation, buf, bvb.getBinaryValues().createByte());

            try {
                if (acceptConditions.isPresent()
                        && !acceptConditions.get().acceptIteration(next, bvb.getBinaryValues())) {
                    break;
                }
            } catch (OperationFailedException e) {
                throw new CreateException(e);
            }

            buf = next;
        }
        return BinaryVoxelsFactory.reuseByte(buf, bvb.getBinaryValues());
    }

    private static BinaryKernel createDilationKernel(
            BinaryValuesByte bv,
            boolean do3D,
            Optional<Voxels<ByteBuffer>> backgroundVb,
            int minIntensityValue,
            boolean zOnly,
            boolean outsideAtThreshold,
            boolean bigNeighborhood)
            throws CreateException {

        BinaryKernel kernelDilation;

        if (zOnly) {

            if (bigNeighborhood) {
                throw new CreateException("Big-neighborhood not supported for zOnly");
            }

            kernelDilation = new DilationKernel3ZOnly(bv, outsideAtThreshold);
        } else {
            kernelDilation = new DilationKernel3(bv, outsideAtThreshold, do3D, bigNeighborhood);
        }

        // TODO HACK FIX , how we handle the different regions

        if (minIntensityValue > 0 && backgroundVb.isPresent()) {
            return new ConditionalKernel(kernelDilation, minIntensityValue, backgroundVb.get());
        } else {
            return kernelDilation;
        }
    }
}
