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
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.kernel.count.CountKernel;
import org.anchoranalysis.image.voxel.object.ObjectMask;
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

    private static final VoxelsFactoryTypeBound<UnsignedByteBuffer> FACTORY =
            VoxelsFactory.getUnsignedByte();

    public static Voxels<UnsignedByteBuffer> apply(
            BinaryKernel kernel, Voxels<UnsignedByteBuffer> in) {
        return apply(kernel, in, BinaryValuesByte.getDefault());
    }

    // 3 pixel diameter kernel
    public static Voxels<UnsignedByteBuffer> apply(
            BinaryKernel kernel, Voxels<UnsignedByteBuffer> in, BinaryValuesByte outBinary) {

        Voxels<UnsignedByteBuffer> out = FACTORY.createInitialized(in.extent());

        int localSlicesSize = 3;

        Extent extent = in.extent();

        kernel.init(in);

        Point3i point = new Point3i();
        for (point.setZ(0); point.z() < extent.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, in);
            UnsignedByteBuffer outArr = out.sliceBuffer(point.z());

            int ind = 0;

            kernel.notifyZChange(localSlices, point.z());

            for (point.setY(0); point.y() < extent.y(); point.incrementY()) {
                for (point.setX(0); point.x() < extent.x(); point.incrementX()) {

                    if (kernel.acceptPoint(ind, point)) {
                        outArr.putRaw(ind, outBinary.getOnByte());
                    } else {
                        outArr.putRaw(ind, outBinary.getOffByte());
                    }

                    ind++;
                }
            }
        }

        return out;
    }

    /**
     * Applies the kernel to voxels and sums the returned value
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(CountKernel kernel, Voxels<UnsignedByteBuffer> voxels)
            throws OperationFailedException {
        return applyForCount(kernel, voxels, new BoundingBox(voxels.extent()));
    }

    /**
     * Applies the kernel to voxels and sums the returned value
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @param box a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be containted within voxels.
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(
            CountKernel kernel, Voxels<UnsignedByteBuffer> voxels, BoundingBox box)
            throws OperationFailedException {

        if (!voxels.extent().contains(box)) {
            throw new OperationFailedException(
                    String.format(
                            "BBox (%s) must be contained within extent (%s)",
                            box, voxels.extent()));
        }

        int localSlicesSize = 3;

        int cnt = 0;

        Extent extent = voxels.extent();

        kernel.init(voxels);

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
                    cnt += kernel.countAtPosition(ind, point);
                }
            }
        }

        return cnt;
    }

    /**
     * Applies the kernel to voxels until a positive value is returned, then exits with true
     *
     * @param kernel the kernel to be applied
     * @param voxels the voxels to iterate over
     * @param box a bounding-box (coordinates relative to voxels) that restricts where iteration
     *     occurs. Must be contained within voxels.
     * @return true if a positive-value is encountered, 0 if it never is encountered
     * @throws OperationFailedException
     */
    public static boolean applyUntilPositive(
            CountKernel kernel, Voxels<UnsignedByteBuffer> voxels, BoundingBox box)
            throws OperationFailedException {

        if (!voxels.extent().contains(box)) {
            throw new OperationFailedException(
                    String.format(
                            "Bounding-box (%s) must be contained within extent (%s)",
                            box, voxels.extent()));
        }

        int localSlicesSize = 3;

        Extent extent = voxels.extent();

        kernel.init(voxels);

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

    public static int applyForCount(BinaryKernel kernel, Voxels<UnsignedByteBuffer> in) {

        int localSlicesSize = 3;

        int cnt = 0;

        Extent extent = in.extent();

        kernel.init(in);

        Point3i point = new Point3i();
        for (point.setZ(0); point.z() < extent.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, in);
            kernel.notifyZChange(localSlices, point.z());

            int ind = 0;

            for (point.setY(0); point.y() < extent.y(); point.incrementY()) {
                for (point.setX(0); point.x() < extent.x(); point.incrementX()) {

                    if (kernel.acceptPoint(ind, point)) {
                        cnt++;
                    }

                    ind++;
                }
            }
        }

        return cnt;
    }

    public static int applyForCountOnMask(
            BinaryKernel kernel, Voxels<UnsignedByteBuffer> in, ObjectMask object) {

        int localSlicesSize = 3;

        int count = 0;

        BoundingBox box = object.boundingBox();
        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();

        Extent extent = in.extent();

        kernel.init(in);

        BinaryValuesByte bvb = object.binaryValues().createByte();

        Point3i point = new Point3i();
        for (point.setZ(cornerMin.z()); point.z() <= cornerMax.z(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.z(), localSlicesSize, in);
            kernel.notifyZChange(localSlices, point.z());

            int ind = 0;

            UnsignedByteBuffer bufMask = object.sliceBufferGlobal(point.z());

            for (point.setY(cornerMin.y()); point.y() <= cornerMax.y(); point.incrementY()) {
                for (point.setX(cornerMin.x()); point.x() <= cornerMax.x(); point.incrementX()) {

                    int indKernel = extent.offsetSlice(point);

                    if (bufMask.getRaw(ind) == bvb.getOnByte()
                            && kernel.acceptPoint(indKernel, point)) {
                        count++;
                    }

                    ind++;
                }
            }
        }

        return count;
    }
}
