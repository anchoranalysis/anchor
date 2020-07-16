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

package org.anchoranalysis.image.voxel.iterator;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbor;
import org.anchoranalysis.image.voxel.neighborhood.Neighborhood;

/**
 * Iterate over voxels in an extent/bounding-box/mask calling a processor on each selected voxel
 *
 * @author Owen Feehan
 */
public class IterateVoxels {

    private IterateVoxels() {}

    /**
     * Iterate over each voxel that is located on a mask AND optionally a second-mask
     *
     * <p>If a second-mask is defined, it is a logical AND condition. A voxel is only processed if
     * it exists in both masks.
     *
     * @param firstMask the first-mask that is used as a condition on what voxels to iterate
     * @param secondMask an optional second-mask that can be a further condition
     * @param process is called for each voxel with that satisfies the conditions using GLOBAL
     *     coordinates for each voxel.
     */
    public static void overMasks(
            ObjectMask firstMask, Optional<ObjectMask> secondMask, ProcessVoxel process) {
        if (secondMask.isPresent()) {
            Optional<BoundingBox> intersection =
                    firstMask
                            .getBoundingBox()
                            .intersection()
                            .with(secondMask.get().getBoundingBox());
            intersection.ifPresent(
                    bbox ->
                            callEachPoint(
                                    bbox,
                                    requireIntersectionTwice(
                                            process, firstMask, secondMask.get())));
        } else {
            callEachPoint(firstMask, process);
        }
    }

    /**
     * Iterate over each voxel in a sliding-buffer, optionally restricting it to be only voxels in a
     * certain mask
     *
     * @param buffer a sliding-buffer whose voxels are iterated over, parially (if a mask is
     *     defined) as a whole (if no mask is defined)
     * @param mask an optional mask that is used as a condition on what voxels to iterate
     * @param process process is called for each voxel (on the entire {@link SlidingBuffer} or on
     *     the object-mask depending) using GLOBAL coordinates.
     */
    public static void callEachPoint(
            Optional<ObjectMask> mask, SlidingBuffer<?> buffer, ProcessVoxel process) {

        buffer.seek(mask.map(object -> object.getBoundingBox().cornerMin().getZ()).orElse(0));

        callEachPoint(mask, buffer.extent(), new ProcessVoxelSlide(buffer, process));
    }

    /**
     * Iterate over each voxel that is located on a mask if it exists, otherwise iterate over the
     * entire extent
     *
     * @param mask an optional mask that is used as a condition on what voxels to iterate
     * @param extent if mask isn't defined, then all the voxels in this {@link Extent} are iterated
     *     over instead
     * @param process process is called for each voxel (on the entire {@link Extent} or on the
     *     object-mask depending) using GLOBAL coordinates.
     */
    public static void callEachPoint(
            Optional<ObjectMask> mask, Extent extent, ProcessVoxel process) {
        if (mask.isPresent()) {
            callEachPoint(mask.get(), process);
        } else {
            callEachPoint(extent, process);
        }
    }

    /**
     * Iterate over each voxel that is located on a mask
     *
     * @param mask the mask that is used as a condition on what voxels to iterate
     * @param process process is called for each voxel with that satisfies the conditions using
     *     GLOBAL coordinates.
     */
    public static void callEachPoint(ObjectMask mask, ProcessVoxel process) {
        callEachPoint(mask.getBoundingBox(), new RequireIntersectionWithMask(process, mask));
    }

    /**
     * Iterate over each voxel that is located on a mask - with offsets
     *
     * <p>This is identical to the other {@link callEachPoint} but adds offsets, and is optimized
     * for this circumstance.
     *
     * @param mask the mask that is used as a condition on what voxels to iterate
     * @param extent the scene-size
     * @param process process is called for each voxel with that satisfies the conditions using
     *     GLOBAL coordinates.
     */
    private static <T extends Buffer> void callEachPoint(
            ObjectMask mask, VoxelBox<T> voxels, ProcessVoxelSliceBuffer<T> process) {
        // This is re-implemented in full, as reusing existing code with {@link AddOffsets} and
        //  {@link RequireIntersectionWithMask} was not inling using default JVM settings
        // Based on unit-tests, it seems to perform better emperically, even with the new Point3i()
        // adding to the heap.

        Extent extent = voxels.extent();
        Extent extentMask = mask.getVoxelBox().extent();
        ReadableTuple3i cornerMin = mask.getBoundingBox().cornerMin();
        byte valueOn = mask.getBinaryValuesByte().getOnByte();

        for (int z = 0; z < extentMask.getZ(); z++) {

            // For 3d we need to translate the global index back to local
            int z1 = cornerMin.getZ() + z;

            T bb = voxels.getPixelsForPlane(z1).buffer();
            ByteBuffer bbOM = mask.getVoxelBox().getPixelsForPlane(z).buffer();
            process.notifyChangeZ(z1);

            for (int y = 0; y < extentMask.getY(); y++) {
                int y1 = cornerMin.getY() + y;
                int offset = extent.offset(cornerMin.getX(), y1);

                for (int x = 0; x < extentMask.getX(); x++) {

                    if (bbOM.get() == valueOn) {
                        int x1 = cornerMin.getX() + x;

                        process.process(new Point3i(x1, y1, z1), bb, offset);
                    }
                    offset++;
                }
            }
        }
    }

    /**
     * Calls each voxel in a mask until a point is found
     *
     * @param mask mask
     * @return the first point found
     */
    public static Optional<Point3i> findFirstPointOnMask(ObjectMask mask) {

        Extent extentMask = mask.getVoxelBox().extent();
        ReadableTuple3i cornerMin = mask.getBoundingBox().cornerMin();
        byte valueOn = mask.getBinaryValuesByte().getOnByte();

        for (int z = 0; z < extentMask.getZ(); z++) {

            // For 3d we need to translate the global index back to local
            int z1 = cornerMin.getZ() + z;

            ByteBuffer bbOM = mask.getVoxelBox().getPixelsForPlane(z).buffer();

            for (int y = 0; y < extentMask.getY(); y++) {
                int y1 = cornerMin.getY() + y;

                for (int x = 0; x < extentMask.getX(); x++) {

                    if (bbOM.get() == valueOn) {
                        int x1 = cornerMin.getX() + x;
                        return Optional.of(new Point3i(x1, y1, z1));
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Iterate over each voxel in an {@link Extent}
     *
     * @param extent the extent to be iterated over
     * @param process process is called for each voxel inside the extent using the same coordinates
     *     as the extent.
     */
    public static void callEachPoint(Extent extent, ProcessVoxel process) {
        callEachPoint(new BoundingBox(extent), process);
    }

    /**
     * Iterate over each voxel in a bounding-box
     *
     * @param bbox the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     */
    public static void callEachPoint(BoundingBox bbox, ProcessVoxel process) {

        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMax = bbox.calcCornerMax();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.getZ()); point.getZ() <= cornerMax.getZ(); point.incrementZ()) {

            process.notifyChangeZ(point.getZ());

            for (point.setY(cornerMin.getY());
                    point.getY() <= cornerMax.getY();
                    point.incrementY()) {

                process.notifyChangeY(point.getY());

                for (point.setX(cornerMin.getX());
                        point.getX() <= cornerMax.getX();
                        point.incrementX()) {
                    process.process(point);
                }
            }
        }
    }

    /**
     * Iterate over each voxel - with an associated buffer for each slice from a voxel-bo
     *
     * @param voxels a voxel-box, all of whose voxels will be iterated over
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type in voxel-box
     */
    public static <T extends Buffer> void callEachPoint(
            VoxelBox<T> voxels, ProcessVoxelSliceBuffer<T> process) {
        Extent extent = voxels.extent();
        callEachPoint(extent, new RetrieveBufferForSlice<>(voxels, process));
    }

    /**
     * Iterate over each voxel in a bounding-box - with an associated buffer for each slice from a
     * voxel-bo
     *
     * @param voxels a voxel-box for which {@link} refers to a subregion.
     * @param bbox the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type in voxel-box
     */
    public static <T extends Buffer> void callEachPoint(
            VoxelBox<T> voxels, BoundingBox bbox, ProcessVoxelSliceBuffer<T> process) {
        callEachPoint(bbox, new RetrieveBufferForSlice<>(voxels, process));
    }

    /**
     * Iterate over each voxel in a mask - with an associated buffer for each slice from a voxel-bo
     *
     * @param voxels a voxel-box for which {@link} refers to a subregion.
     * @param mask the mask is used as a condition on what voxels to iterate i.e. only voxels within
     *     these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type in voxel-box
     */
    public static <T extends Buffer> void callEachPoint(
            VoxelBox<T> voxels, ObjectMask mask, ProcessVoxelSliceBuffer<T> process) {
        callEachPoint(mask, voxels, process);
    }

    /**
     * Iterate over each voxel in a mask - with an associated buffer for each slice from a voxel-bo
     *
     * @param voxels a voxel-box for which {@link} refers to a subregion.
     * @param mask the mask is used as a condition on what voxels to iterate i.e. only voxels within
     *     these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type in voxel-box
     */
    public static <T extends Buffer> void callEachPoint(
            VoxelBox<T> voxels, Mask mask, ProcessVoxelSliceBuffer<T> process) {
        // Treat it as one giant object box. This will involve some additions and subtractions of 0
        // during the processing of voxels
        // but after some quick emperical checks, it doesn't seem to make a performance difference.
        // Probably the JVM is smart enough
        // to optimize away these redundant calcualations.
        callEachPoint(new ObjectMask(mask.binaryVoxelBox()), voxels, process);
    }

    /**
     * Iterate over each voxel that is located on a mask if it exists, otherwise iterate over the
     * entire voxel-box.
     *
     * <p>This is similar behaviour to {@link #callEachPoint} but adds a buffer for each slice.
     */
    public static <T extends Buffer> void callEachPoint(
            Optional<ObjectMask> mask, VoxelBox<T> voxels, ProcessVoxelSliceBuffer<T> process) {
        Extent extent = voxels.extent();

        // Note the offsets must be added before any additional restriction like a mask, to make
        // sure they are calculate for EVERY process.
        // Therefore we {@link AddOffsets} must be interested as the top-most level in the
        // processing chain
        // (i.e. {@link AddOffsets} must delegate to {@link RequireIntersectionWithMask} but not the
        // other way round.
        if (mask.isPresent()) {
            callEachPoint(mask.get(), voxels, process);
        } else {
            callEachPoint(extent, new RetrieveBufferForSlice<T>(voxels, process));
        }
    }

    /**
     * Iterate over each point in the neighborhood of an existing point - also setting the source of
     * a delegate
     *
     * @param sourcePoint the point to iterate over its neighborhood
     * @param neighborhood a definition of what constitutes the neighborhood
     * @param do3D whether to iterate in 2D or 3D
     * @param process is called for each voxel in the neighborhood of the source-point.
     * @return the result after processing each point in the neighborhood
     */
    public static <T> T callEachPointInNeighborhood(
            Point3i sourcePoint,
            Neighborhood neighborhood,
            boolean do3D,
            ProcessVoxelNeighbor<T> process,
            int sourceVal,
            int sourceOffsetXY) {
        process.initSource(sourcePoint, sourceVal, sourceOffsetXY);
        neighborhood.processAllPointsInNeighborhood(do3D, process);
        return process.collectResult();
    }

    private static ProcessVoxel requireIntersectionTwice(
            ProcessVoxel processor, ObjectMask mask1, ObjectMask mask2) {
        ProcessVoxel inner = new RequireIntersectionWithMask(processor, mask2);
        return new RequireIntersectionWithMask(inner, mask1);
    }
}
