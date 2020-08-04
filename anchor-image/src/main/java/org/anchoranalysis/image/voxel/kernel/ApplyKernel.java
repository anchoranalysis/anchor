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

import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.kernel.count.CountKernel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Applies a kernel to a Voxel Box
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ApplyKernel {

    private static final VoxelBoxFactoryTypeBound<ByteBuffer> FACTORY = VoxelBoxFactory.getByte();

    public static VoxelBox<ByteBuffer> apply(BinaryKernel kernel, VoxelBox<ByteBuffer> in) {
        return apply(kernel, in, BinaryValuesByte.getDefault());
    }

    // 3 pixel diameter kernel
    public static VoxelBox<ByteBuffer> apply(
            BinaryKernel kernel, VoxelBox<ByteBuffer> in, BinaryValuesByte outBinary) {

        VoxelBox<ByteBuffer> out = FACTORY.create(in.extent());

        int localSlicesSize = 3;

        Extent extent = in.extent();

        kernel.init(in);

        Point3i point = new Point3i();
        for (point.setZ(0); point.getZ() < extent.getZ(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.getZ(), localSlicesSize, in);
            ByteBuffer outArr = out.getPixelsForPlane(point.getZ()).buffer();

            int ind = 0;

            kernel.notifyZChange(localSlices, point.getZ());

            for (point.setY(0); point.getY() < extent.getY(); point.incrementY()) {
                for (point.setX(0); point.getX() < extent.getX(); point.incrementX()) {

                    if (kernel.accptPos(ind, point)) {
                        outArr.put(ind, outBinary.getOnByte());
                    } else {
                        outArr.put(ind, outBinary.getOffByte());
                    }

                    ind++;
                }
            }
        }

        return out;
    }

    /**
     * Applies the kernel to a voxelbox and sums the returned value
     *
     * @param kernel the kernel to be applied
     * @param vb the voxel-box to iterate over
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(CountKernel kernel, VoxelBox<ByteBuffer> vb)
            throws OperationFailedException {
        return applyForCount(kernel, vb, new BoundingBox(vb.extent()));
    }

    /**
     * Applies the kernel to a voxelbox and sums the returned value
     *
     * @param kernel the kernel to be applied
     * @param vb the voxel-box to iterate over
     * @param bbox a bounding-box (coordinates relative to vb) that restricts where iteration
     *     occurs. Must be containted within vb.
     * @return the sum of the count value returned by the kernel over all iterated voxels
     * @throws OperationFailedException
     */
    public static int applyForCount(CountKernel kernel, VoxelBox<ByteBuffer> vb, BoundingBox bbox)
            throws OperationFailedException {

        if (!vb.extent().contains(bbox)) {
            throw new OperationFailedException(
                    String.format(
                            "BBox (%s) must be contained within extent (%s)", bbox, vb.extent()));
        }

        int localSlicesSize = 3;

        int cnt = 0;

        Extent extent = vb.extent();

        kernel.init(vb);

        ReadableTuple3i pointMax = bbox.calcCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(bbox.cornerMin().getZ());
                point.getZ() <= pointMax.getZ();
                point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.getZ(), localSlicesSize, vb);
            kernel.notifyZChange(localSlices, point.getZ());

            for (point.setY(bbox.cornerMin().getY());
                    point.getY() <= pointMax.getY();
                    point.incrementY()) {
                for (point.setX(bbox.cornerMin().getX());
                        point.getX() <= pointMax.getX();
                        point.incrementX()) {

                    int ind = extent.offset(point.getX(), point.getY());
                    cnt += kernel.countAtPos(ind, point);
                }
            }
        }

        return cnt;
    }

    /**
     * Applies the kernel to a voxelbox until a positive value is returned, then exits with TRUE
     *
     * @param kernel the kernel to be applied
     * @param vb the voxel-box to iterate over
     * @param bbox a bounding-box (coordinates relative to vb) that restricts where iteration
     *     occurs. Must be containted within vb.
     * @return TRUE if a positive-value is encountered, 0 if it never is encountered
     * @throws OperationFailedException
     */
    public static boolean applyUntilPositive(
            CountKernel kernel, VoxelBox<ByteBuffer> vb, BoundingBox bbox)
            throws OperationFailedException {

        if (!vb.extent().contains(bbox)) {
            throw new OperationFailedException(
                    String.format(
                            "BBox (%s) must be contained within extent (%s)", bbox, vb.extent()));
        }

        int localSlicesSize = 3;

        Extent extent = vb.extent();

        kernel.init(vb);

        ReadableTuple3i pointMax = bbox.calcCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(bbox.cornerMin().getZ());
                point.getZ() <= pointMax.getZ();
                point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.getZ(), localSlicesSize, vb);
            kernel.notifyZChange(localSlices, point.getZ());

            for (point.setY(bbox.cornerMin().getY());
                    point.getY() <= pointMax.getY();
                    point.incrementY()) {
                for (point.setX(bbox.cornerMin().getX());
                        point.getX() <= pointMax.getX();
                        point.incrementX()) {

                    int ind = extent.offsetSlice(point);
                    if (kernel.countAtPos(ind, point) > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int applyForCount(BinaryKernel kernel, VoxelBox<ByteBuffer> in) {

        int localSlicesSize = 3;

        int cnt = 0;

        Extent extent = in.extent();

        kernel.init(in);

        Point3i point = new Point3i();
        for (point.setZ(0); point.getZ() < extent.getZ(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.getZ(), localSlicesSize, in);
            kernel.notifyZChange(localSlices, point.getZ());

            int ind = 0;

            for (point.setY(0); point.getY() < extent.getY(); point.incrementY()) {
                for (point.setX(0); point.getX() < extent.getX(); point.incrementX()) {

                    if (kernel.accptPos(ind, point)) {
                        cnt++;
                    }

                    ind++;
                }
            }
        }

        return cnt;
    }

    public static int applyForCountOnMask(
            BinaryKernel kernel, VoxelBox<ByteBuffer> in, ObjectMask object) {

        int localSlicesSize = 3;

        int cnt = 0;

        BoundingBox bbox = object.getBoundingBox();
        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMax = bbox.calcCornerMax();

        Extent extent = in.extent();

        kernel.init(in);

        BinaryValuesByte bvb = object.getBinaryValues().createByte();

        Point3i point = new Point3i();
        for (point.setZ(cornerMin.getZ()); point.getZ() <= cornerMax.getZ(); point.incrementZ()) {

            LocalSlices localSlices = new LocalSlices(point.getZ(), localSlicesSize, in);
            kernel.notifyZChange(localSlices, point.getZ());

            int ind = 0;

            ByteBuffer bufMask =
                    object.getVoxels()
                            .getPixelsForPlane(point.getZ() - cornerMin.getZ())
                            .buffer();

            for (point.setY(cornerMin.getY());
                    point.getY() <= cornerMax.getY();
                    point.incrementY()) {
                for (point.setX(cornerMin.getX());
                        point.getX() <= cornerMax.getX();
                        point.incrementX()) {

                    int indKernel = extent.offset(point.getX(), point.getY());

                    if (bufMask.get(ind) == bvb.getOnByte() && kernel.accptPos(indKernel, point)) {
                        cnt++;
                    }

                    ind++;
                }
            }
        }

        return cnt;
    }
}
