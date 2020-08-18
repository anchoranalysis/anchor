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
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.iterator.changed.ProcessVoxelNeighbor;
import org.anchoranalysis.image.voxel.neighborhood.Neighborhood;
import com.google.common.base.Preconditions;

/**
 * Iterate over voxels in an extent/bounding-box/mask calling a processor on each selected voxel
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxels {

    /**
     * Iterate over each voxel that is located on an object-mask AND optionally a second-mask
     *
     * <p>If a second object-mask is defined, it is a logical AND condition. A voxel is only
     * processed if it exists in both object-masks.
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
                    firstMask.boundingBox().intersection().with(secondMask.get().boundingBox());
            intersection.ifPresent(
                    box ->
                            callEachPoint(
                                    box,
                                    requireIntersectionTwice(
                                            process, firstMask, secondMask.get())));
        } else {
            callEachPoint(firstMask, process);
        }
    }

    /**
     * Iterate over each voxel in a sliding-buffer, optionally restricting it to be only voxels in a
     * certain object
     *
     * @param buffer a sliding-buffer whose voxels are iterated over, partially (if an objectmask is
     *     defined) or as a whole (if no onject-mask is defined)
     * @param objectMask an optional object-mask that is used as a condition on what voxels to
     *     iterate
     * @param process process is called for each voxel (on the entire {@link SlidingBuffer} or on
     *     the object-mask depending) using GLOBAL coordinates.
     */
    public static void callEachPoint(
            Optional<ObjectMask> objectMask, SlidingBuffer<?> buffer, ProcessVoxel process) {

        buffer.seek(objectMask.map(object -> object.boundingBox().cornerMin().z()).orElse(0));

        callEachPoint(objectMask, buffer.extent(), new SlidingBufferProcessor(buffer, process));
    }

    /**
     * Iterate over each voxel that is located on a object-mask if it exists, otherwise iterate over
     * the entire extent
     *
     * @param objectMask an optional object-mask that is used as a condition on what voxels to
     *     iterate
     * @param extent if object-mask isn't defined, then all the voxels in this {@link Extent} are
     *     iterated over instead
     * @param process process is called for each voxel (on the entire {@link Extent} or on the
     *     object-mask depending) using GLOBAL coordinates.
     */
    public static void callEachPoint(
            Optional<ObjectMask> objectMask, Extent extent, ProcessVoxel process) {
        if (objectMask.isPresent()) {
            callEachPoint(objectMask.get(), process);
        } else {
            callEachPoint(extent, process);
        }
    }

    /**
     * Iterate over each voxel that is located on an object-mask
     *
     * @param object the object-mask that is used as a condition on what voxels to iterate
     * @param process process is called for each voxel with that satisfies the conditions using
     *     GLOBAL coordinates.
     */
    public static void callEachPoint(ObjectMask object, ProcessVoxel process) {
        callEachPoint(object.boundingBox(), new RequireIntersectionWithObject(process, object));
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
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     */
    public static void callEachPoint(BoundingBox box, ProcessVoxel process) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.z()); point.z() <= cornerMax.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() <= cornerMax.y(); point.incrementY()) {

                process.notifyChangeY(point.y());

                for (point.setX(cornerMin.x()); point.x() <= cornerMax.x(); point.incrementX()) {
                    process.process(point);
                }
            }
        }
    }

    /**
     * Iterate over each voxel in an extent that matches a predicate
     *
     * @param box the extent through which every point is tested to see if it matches the predicate
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     */
    public static void callEachPoint(
            Extent extent, Predicate<Point3i> predicate, ProcessVoxel process) {
        callEachPoint(new BoundingBox(extent), predicate, process);
    }

    /**
     * Iterate over each voxel in a bounding-box that matches a predicate
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     */
    public static void callEachPoint(
            BoundingBox box, Predicate<Point3i> predicate, ProcessVoxel process) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.z()); point.z() <= cornerMax.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() <= cornerMax.y(); point.incrementY()) {

                process.notifyChangeY(point.y());

                for (point.setX(cornerMin.x()); point.x() <= cornerMax.x(); point.incrementX()) {
                    if (predicate.test(point)) {
                        process.process(point);
                    }
                }
            }
        }
    }

    /**
     * Iterate over each voxel - with an associated buffer for each slice from a voxel-buffer
     *
     * @param voxels voxels to be iterated over (in their entirity)
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T extends Buffer> void callEachPoint(
            Voxels<T> voxels, ProcessVoxelSliceBuffer<T> process) {
        callEachPoint(voxels.extent(), new RetrieveBufferForSlice<>(voxels, process));
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>one</b> associated buffer for each slice
     *
     * @param voxels voxels in which which {@link BoundingBox} refers to a subregion.
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T extends Buffer> void callEachPoint(
            Voxels<T> voxels, BoundingBox box, ProcessVoxelSliceBuffer<T> process) {
        callEachPoint(box, new RetrieveBufferForSlice<>(voxels, process));
    }
    
    /**
     * Iterate over each voxel in a bounding-box - with <b>two</b> associated buffers for each slice
     * <p>
     * The extent's of both {@code voxels1} and {@code voxels2} must be equal.
     * 
     * @param voxels1 voxels in which which {@link BoundingBox} refers to a subregion, and which provides the <b>first</b> buffer
     * @param voxels2 voxels in which which {@link BoundingBox} refers to a subregion, and which provides the <b>second</b> buffer
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T extends Buffer> void callEachPointTwo(
            Voxels<T> voxels1, Voxels<T> voxels2, ProcessVoxelTwoSliceBuffer<T> process) {
        Preconditions.checkArgument( voxels1.extent().equals(voxels2.extent()) );
        callEachPoint(voxels1.extent(), new RetrieveBuffersForTwoSlices<>(voxels1, voxels2, process));
    }

    /**
     * Iterate over each voxel in an object-mask - with an associated buffer for each slice from
     * {@link Voxels}
     *
     * @param voxels voxels where buffers extracted from be processed, and which define the global
     *     coordinate space
     * @param object the object-mask is used as a condition on what voxels to iterate i.e. only
     *     voxels within these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T extends Buffer> void callEachPoint(
            Voxels<T> voxels, ObjectMask object, ProcessVoxelSliceBuffer<T> process) {
        /**
         * This is re-implemented in full, as reusing existing code with {@link AddOffsets} and /
         * {@link RequireIntersectionWithMask} was not inlining using default JVM settings / Based
         * on unit-tests, it seems to perform better empirically, even with the new Point3i() /
         * adding to the heap.
         */
        Extent extent = voxels.extent();

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        byte valueOn = object.binaryValuesByte().getOnByte();

        Point3i cornerMax = object.boundingBox().calculateCornerMaxExclusive();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            T buffer = voxels.sliceBuffer(point.z());
            ByteBuffer bufferObject = object.sliceBufferGlobal(point.z());
            process.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {

                int offset = extent.offset(cornerMin.x(), point.y());

                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {

                    if (bufferObject.get() == valueOn) {
                        process.process(point, buffer, offset);
                    }
                    offset++;
                }
            }
        }
    }
    
    /**
     * Iterate over each voxel in a mask - with an associated buffer for each slice from a voxel-bo
     *
     * @param voxels voxels for which {@link} refers to a subregion.
     * @param mask the mask is used as a condition on what voxels to iterate i.e. only voxels within
     *     these bounds
     * @param process is called for each voxel within the bounding-box using GLOBAL coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T extends Buffer> void callEachPoint(
            Voxels<T> voxels, Mask mask, ProcessVoxelSliceBuffer<T> process) {
        // Treat it as one giant object box. This will involve some additions and subtractions of 0
        // during the processing of voxels
        // but after some quick emperical checks, it doesn't seem to make a performance difference.
        // Probably the JVM is smart enough
        // to optimize away these redundant calcualations.
        callEachPoint(voxels, new ObjectMask(mask.binaryVoxels()), process);
    }

    /**
     * Iterate over each voxel that is located on an object-mask if it exists, otherwise iterate
     * over the entire voxels.
     *
     * <p>This is similar behaviour to {@link #callEachPoint} but adds a buffer for each slice.
     */
    public static <T extends Buffer> void callEachPoint(
            Voxels<T> voxels, Optional<ObjectMask> objectMask, ProcessVoxelSliceBuffer<T> process) {
        Extent extent = voxels.extent();

        // Note the offsets must be added before any additional restriction like an object-mask, to
        // make
        // sure they are calculated for EVERY process.
        // Therefore we {@link AddOffsets} must be interested as the top-most level in the
        // processing chain
        // (i.e. {@link AddOffsets} must delegate to {@link RequireIntersectionWithMask} but not the
        // other way round.
        if (objectMask.isPresent()) {
            callEachPoint(voxels, objectMask.get(), process);
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
            ProcessVoxel processor, ObjectMask object1, ObjectMask object2) {
        ProcessVoxel inner = new RequireIntersectionWithObject(processor, object2);
        return new RequireIntersectionWithObject(inner, object1);
    }
}
