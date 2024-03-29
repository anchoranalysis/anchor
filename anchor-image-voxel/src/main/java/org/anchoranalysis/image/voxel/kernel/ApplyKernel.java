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

package org.anchoranalysis.image.voxel.kernel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.iterator.neighbor.kernel.IterateKernelHelper;
import org.anchoranalysis.image.voxel.iterator.process.ProcessKernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.count.CountKernel;
import org.anchoranalysis.math.arithmetic.Counter;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Applies various kinds of {@link Kernel} to {@link BinaryVoxels}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplyKernel {

    private static final VoxelsFactoryTypeBound<UnsignedByteBuffer> FACTORY =
            VoxelsFactory.getUnsignedByte();

    /**
     * Apply the kernel to {@code BinaryVoxels<UnsignedByteBuffer>} using the same binary-values as
     * {@code voxels} to calculate a value for each voxel.
     *
     * @param kernel the kernel to apply.
     * @param voxels the voxels to apply the kernel on.
     * @param parameters parameters influencing how the kernel is applied.
     * @return a newly created {@code BinaryVoxels<UnsignedByteBuffer>} that is the result of
     *     applying the kernel, and using the same binary-values as {@code voxels}.
     */
    public static BinaryVoxels<UnsignedByteBuffer> apply(
            BinaryKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters parameters) {

        Voxels<UnsignedByteBuffer> out = FACTORY.createInitialized(voxels.extent());

        BinaryValuesByte outBinaryValues = voxels.binaryValues().asByte();

        IterateKernelHelper.overAll(
                kernel,
                voxels,
                parameters,
                new ProcessKernelPointCursor() {

                    private UnsignedByteBuffer outBuffer;

                    @Override
                    public void notifyChangeSlice(int z) {
                        outBuffer = out.sliceBuffer(z);
                    }

                    @Override
                    public void process(KernelPointCursor point) {
                        byte outValue =
                                kernel.calculateAt(point)
                                        ? outBinaryValues.getOn()
                                        : outBinaryValues.getOff();
                        outBuffer.putRaw(point.getIndex(), outValue);
                    }
                });

        return BinaryVoxelsFactory.reuseByte(out, outBinaryValues.asInt());
    }

    /**
     * Applies the kernel to voxels and sums the returned value.
     *
     * @param kernel the kernel to be applied.
     * @param voxels the voxels to iterate over.
     * @param parameters parameters that affect how the kernel is applied.
     * @return the sum of the count value returned by the kernel over all iterated voxels.
     */
    public static int applyForCount(
            CountKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters parameters) {
        try {
            return applyForCount(kernel, voxels, new BoundingBox(voxels.extent()), parameters);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Applies the kernel to voxels and sums the returned value.
     *
     * @param kernel the kernel to be applied.
     * @param voxels the voxels to iterate over.
     * @param box a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be contained within voxels.
     * @param parameters parameters that affect how the kernel is applied.
     * @return the sum of the count value returned by the kernel over all iterated voxels.
     * @throws OperationFailedException if the operation cannot complete successfully.
     */
    public static int applyForCount(
            CountKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters parameters)
            throws OperationFailedException {

        Counter counter = new Counter();

        IterateKernelHelper.overBox(
                kernel,
                voxels,
                box,
                parameters,
                point -> counter.incrementBy(kernel.calculateAt(point)));

        return counter.getCount();
    }

    /**
     * Applies the kernel to voxels until a positive value is returned, then exits with true.
     *
     * @param kernel the kernel to be applied.
     * @param voxels the voxels to iterate over.
     * @param box a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be contained within voxels.
     * @param parameters parameters that affect how the kernel is applied.
     * @return true if a positive-value is encountered, 0 if it never is encountered.
     * @throws OperationFailedException if the operation cannot complete successfully.
     */
    public static boolean applyUntilPositive(
            CountKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters parameters)
            throws OperationFailedException {

        return IterateKernelHelper.overBoxUntil(
                kernel, voxels, box, parameters, point -> kernel.calculateAt(point) > 0);
    }

    /**
     * Applies a {@link BinaryKernel} to voxels and counts how many true values occur en aggregate.
     *
     * @param kernel the kernel to be applied.
     * @param voxels the voxels to iterate over.
     * @param parameters parameters that affect how the kernel is applied.
     * @return the total number of true values, after the kernel is applied to every voxel.
     */
    public static int applyForCount(
            BinaryKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters parameters) {

        Counter counter = new Counter();

        IterateKernelHelper.overAll(
                kernel,
                voxels,
                parameters,
                point -> {
                    if (kernel.calculateAt(point)) {
                        counter.increment();
                    }
                });

        return counter.getCount();
    }
}
