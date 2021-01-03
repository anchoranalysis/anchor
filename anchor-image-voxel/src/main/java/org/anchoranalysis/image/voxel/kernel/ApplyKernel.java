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
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.image.voxel.iterator.process.ProcessKernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.count.CountKernel;
import org.anchoranalysis.math.arithmetic.Counter;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Applies a kernel to a Voxel Box
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplyKernel {

    private static final int LOCAL_SLICES_SIZE = 3;

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
        kernel.init(in.voxels(), params);

        KernelPointCursor cursor = new KernelPointCursor(0, new Point3i(), in.extent(), in.binaryValues().createByte(), params);
        
        IterateVoxelsAll.withCursor(cursor, new ProcessKernelPointCursor() {
            
            private LocalSlices localSlices;
            private UnsignedByteBuffer outBuffer;
            
            @Override
            public void notifyChangeSlice(int z) {
                localSlices = new LocalSlices(cursor.getPoint().z(), LOCAL_SLICES_SIZE, in.voxels());
                outBuffer = out.sliceBuffer(z);
                kernel.notifyZChange(localSlices, cursor.getPoint().z());
            }
            
            @Override
            public void process(KernelPointCursor point) {
                byte outValue =
                        kernel.acceptPoint(cursor)
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
     * @throws OperationFailedException
     */
    public static int applyForCount(
            CountKernel kernel,
            Voxels<UnsignedByteBuffer> voxels,
            KernelApplicationParameters params)
            throws OperationFailedException {
        return applyForCount(kernel, voxels, new BoundingBox(voxels.extent()), params);
    }

    /**
     * Applies the kernel to voxels and sums the returned value.
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @param box a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be containted within voxels.
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(
            CountKernel kernel,
            Voxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params)
            throws OperationFailedException {

        if (!voxels.extent().contains(box)) {
            throw new OperationFailedException(
                    String.format(
                            "BBox (%s) must be contained within extent (%s)",
                            box, voxels.extent()));
        }

        int localSlicesSize = 3;

        int count = 0;

        Extent extent = voxels.extent();

        kernel.init(voxels, params);

        ReadableTuple3i pointMax = box.calculateCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() <= pointMax.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, voxels);
            kernel.notifyZChange(localSlices, point.z());

            for (point.setY(box.cornerMin().y()); point.y() <= pointMax.y(); point.incrementY()) {
                for (point.setX(box.cornerMin().x());
                        point.x() <= pointMax.x();
                        point.incrementX()) {

                    int ind = extent.offsetSlice(point);
                    count += kernel.countAtPosition(ind, point);
                }
            }
        }

        return count;
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
            Voxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params)
            throws OperationFailedException {

        if (!voxels.extent().contains(box)) {
            throw new OperationFailedException(
                    String.format(
                            "Bounding-box (%s) must be contained within extent (%s)",
                            box, voxels.extent()));
        }

        int localSlicesSize = 3;

        Extent extent = voxels.extent();

        kernel.init(voxels, params);

        ReadableTuple3i pointMax = box.calculateCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() <= pointMax.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, voxels);
            kernel.notifyZChange(localSlices, point.z());

            for (point.setY(box.cornerMin().y()); point.y() <= pointMax.y(); point.incrementY()) {
                for (point.setX(box.cornerMin().x());
                        point.x() <= pointMax.x();
                        point.incrementX()) {

                    int ind = extent.offsetSlice(point);
                    if (kernel.countAtPosition(ind, point) > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int applyForCount(
            BinaryKernel kernel,
            BinaryVoxels<UnsignedByteBuffer> in,
            KernelApplicationParameters params) {

        int localSlicesSize = 3;

        Counter counter = new Counter();

        kernel.init(in.voxels(), params);

        KernelPointCursor cursor = new KernelPointCursor(0, new Point3i(), in.extent(), in.binaryValues().createByte(), params);

        IterateVoxelsAll.withCursor(cursor, new ProcessKernelPointCursor() {
            
            private LocalSlices localSlices;
            
            @Override
            public void notifyChangeSlice(int z) {
                localSlices = new LocalSlices(cursor.getPoint().z(), localSlicesSize, in.voxels());
                kernel.notifyZChange(localSlices, cursor.getPoint().z());
            }
            
            @Override
            public void process(KernelPointCursor point) {
                if (kernel.acceptPoint(cursor)) {
                    counter.increment();
                }
            }
        });

        return counter.getCount();
    }
}
