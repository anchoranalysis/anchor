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

    public static BinaryVoxels<UnsignedByteBuffer> apply(
            BinaryKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> in,
            KernelApplicationParameters params) {
        return apply(kernel, in, BinaryValuesByte.getDefault(), params);
    }

    // 3 pixel diameter kernel
    public static BinaryVoxels<UnsignedByteBuffer> apply(
            BinaryKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> in,
            BinaryValuesByte outBinary,
            KernelApplicationParameters params) {

        Voxels<UnsignedByteBuffer> out = FACTORY.createInitialized(in.extent());

        IterateWithKernelHelper.overAll(
                kernel,
                in,
                params,
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
                                        ? outBinary.getOnByte()
                                        : outBinary.getOffByte();
                        outBuffer.putRaw(point.getIndex(), outValue);
                    }
                });

        return BinaryVoxelsFactory.reuseByte(out, outBinary.createInt());
    }

    /**
     * Applies the kernel to voxels and sums the returned value.
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @return the sum of the count value returned by the kernel over all iterated voxels
     */
    public static int applyForCount(
            CountKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters params) {
        try {
            return applyForCount(kernel, voxels, new BoundingBox(voxels.extent()), params);
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Applies the kernel to voxels and sums the returned value.
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @param box a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be contained within voxels.
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(
            CountKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params)
            throws OperationFailedException {
        
        Counter counter = new Counter();

        IterateWithKernelHelper.overBox(
                kernel,
                voxels,
                box,
                params,
                point -> counter.incrementBy( kernel.calculateAt(point) )
        );

        return counter.getCount();
    }

    /**
     * Applies the kernel to voxels until a positive value is returned, then exits with true.
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @param box a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be contained within voxels.
     * @return true if a positive-value is encountered, 0 if it never is encountered
     * @throws OperationFailedException
     */
    public static boolean applyUntilPositive(
            CountKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params)
            throws OperationFailedException {

        return IterateWithKernelHelper.overBoxUntil(
                kernel,
                voxels,
                box,
                params,
                point -> kernel.calculateAt(point) > 0
        );
    }

    public static int applyForCount(
            BinaryKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters params) {

        Counter counter = new Counter();

        IterateWithKernelHelper.overAll(
                kernel,
                voxels,
                params,
                point -> {
                    if (kernel.calculateAt(point)) {
                        counter.increment();
                    }
                });


        return counter.getCount();
    }
}
