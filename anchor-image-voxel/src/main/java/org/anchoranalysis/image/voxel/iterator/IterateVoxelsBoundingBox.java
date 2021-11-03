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
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.predicate.PredicateBufferBinary;
import org.anchoranalysis.image.voxel.iterator.predicate.PredicateKernelPointCursor;
import org.anchoranalysis.image.voxel.iterator.process.ProcessKernelPointCursor;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPoint;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPointAndIndex;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinaryMixed;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferTernary;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnary;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Utilities for iterating over the subset of image voxels within a bounding-box.
 *
 * <p>The utilities operate on one or more {@link Voxels}. A processor is called on each selected
 * voxel.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsBoundingBox {

    /**
     * Iterate over each voxel in a bounding-box
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     */
    public static void withPoint(BoundingBox box, ProcessPoint process) {

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
     * Iterate over each voxel in a bounding-box
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     */
    public static void withPointAndIndex(BoundingBox box, ProcessPointAndIndex process) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.z()); point.z() <= cornerMax.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            int index = 0;

            for (point.setY(cornerMin.y()); point.y() <= cornerMax.y(); point.incrementY()) {

                process.notifyChangeY(point.y());

                for (point.setX(cornerMin.x()); point.x() <= cornerMax.x(); point.incrementX()) {
                    process.process(point, index++);
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box that matches a predicate.
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param predicate only voxels that match this predicate are processed.
     * @param process is called for each voxel that matches {@code predicate} within the
     *     bounding-box using <i>global</i> coordinates.
     */
    public static void withMatchingPoints(
            BoundingBox box, Predicate<Point3i> predicate, ProcessPoint process) {

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
     * Iterate over each voxel in a bounding-box - with <b>one</b> associated buffer for each slice
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param voxels voxels in which which {code box} refers to a subregion.
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withBuffer(
            BoundingBox box, Voxels<T> voxels, ProcessBufferUnary<T> process) {
        withPoint(box, new RetrieveBufferForSlice<>(voxels, process));
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>two</b> associated buffers for each
     * slice, one {@link VoxelBuffer} and one {@link Buffer}
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param shiftForSecond added to the current point in {@code voxels1} to give a corresponding
     *     point in {@code voxels2}.
     * @param voxels1 voxels in which which {@code box} refers to a subregion.
     * @param voxels2 voxels in which which {@code box + shiftForSecond} refers to a subregion.
     * @param process is called for each voxel within the bounding-box where the point uses
     *     <i>global</i> coordinates without the shift. A new {@link Point3i} is <b>not</b> created
     *     on each iteration.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withTwoBuffers(
            BoundingBox box,
            ReadableTuple3i shiftForSecond,
            Voxels<T> voxels1,
            Voxels<T> voxels2,
            ProcessBufferBinary<T, T> process) {
        ReadableTuple3i max = box.calculateCornerMaxExclusive();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() < max.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            T buffer1 = voxels1.sliceBuffer(point.z());
            T buffer2 = voxels2.sliceBuffer(point.z() + shiftForSecond.z());

            for (point.setY(box.cornerMin().y()); point.y() < max.y(); point.incrementY()) {
                int yOther = point.y() + shiftForSecond.y();

                for (point.setX(box.cornerMin().x()); point.x() < max.x(); point.incrementX()) {
                    int xOther = point.x() + shiftForSecond.x();

                    int offset1 = voxels1.extent().offsetSlice(point);
                    int offset2 = voxels2.extent().offset(xOther, yOther);

                    process.process(point, buffer1, buffer2, offset1, offset2);
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>two</b> associated buffers for each
     * slice.
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param shiftForSecond added to the current point in {@code voxels1} to give a corresponding
     *     point in {@code voxels2}.
     * @param voxels1 voxels in which which {@code box} refers to a subregion.
     * @param voxels2 voxels in which which {@code box + shiftForSecond} refers to a subregion.
     * @param process is called for each voxel within the bounding-box where the point uses
     *     <i>global</i> coordinates without the shift. A new {@link Point3i} is <b>not</b> created
     *     on each iteration.
     * @param <S> buffer-type for the <i>voxel-buffer</i> for {@code voxels1}).
     * @param <T> buffer-type for the <i>buffer</i> (for {@code voxels2}).
     */
    public static <S, T> void withTwoMixedBuffers(
            BoundingBox box,
            ReadableTuple3i shiftForSecond,
            Voxels<S> voxels1,
            Voxels<T> voxels2,
            ProcessBufferBinaryMixed<S, T> process) {
        ReadableTuple3i max = box.calculateCornerMaxExclusive();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() < max.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            VoxelBuffer<S> buffer1 = voxels1.slice(point.z());
            T buffer2 = voxels2.sliceBuffer(point.z() + shiftForSecond.z());

            for (point.setY(box.cornerMin().y()); point.y() < max.y(); point.incrementY()) {
                int yOther = point.y() + shiftForSecond.y();

                for (point.setX(box.cornerMin().x()); point.x() < max.x(); point.incrementX()) {
                    int xOther = point.x() + shiftForSecond.x();

                    int offset1 = voxels1.extent().offsetSlice(point);
                    int offset2 = voxels2.extent().offset(xOther, yOther);

                    process.process(point, buffer1, buffer2, offset1, offset2);
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>two</b> associated buffers for each slice
     * - until a predicate evaluates to true.
     *
     * <p>{@code predicate} is called for each voxel within the bounding-box where the point uses
     * <i>global</i> coordinates without the shift while it continues to evaluate to false. The
     * routine exits on the first occasion {@code predicate} evaluates to true. A new {@link
     * Point3i} is <b>not</b> created on each iteration.
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param shiftForSecond added to the current point in {@code voxels1} to give a corresponding
     *     point in {@code voxels2}.
     * @param voxels1 voxels in which which {@code box} refers to a subregion.
     * @param voxels2 voxels in which which {@code box + shiftForSecond} refers to a subregion.
     * @param predicate the predicate as described above.
     * @param <T> buffer-type for voxels
     * @return the current point (relative to the corner of {@code box1} when the predicate first
     *     evaluates to true, or {@link Optional#empty} if no point satisfies the predicate.
     */
    public static <T> Optional<Point3i> withTwoBuffersUntil(
            BoundingBox box,
            ReadableTuple3i shiftForSecond,
            Voxels<T> voxels1,
            Voxels<T> voxels2,
            PredicateBufferBinary<T> predicate) {
        ReadableTuple3i max = box.calculateCornerMaxExclusive();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() < max.z(); point.incrementZ()) {

            predicate.notifyChangeSlice(point.z());

            T buffer1 = voxels1.sliceBuffer(point.z());
            T buffer2 = voxels2.sliceBuffer(point.z() + shiftForSecond.z());

            for (point.setY(box.cornerMin().y()); point.y() < max.y(); point.incrementY()) {
                int yOther = point.y() + shiftForSecond.y();

                for (point.setX(box.cornerMin().x()); point.x() < max.x(); point.incrementX()) {
                    int xOther = point.x() + shiftForSecond.x();

                    int offset1 = voxels1.extent().offsetSlice(point);
                    int offset2 = voxels2.extent().offset(xOther, yOther);

                    if (predicate.test(point, buffer1, buffer2, offset1, offset2)) {
                        return Optional.of(point);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>three</b> associated buffers for each
     * slice.
     *
     * @param box the box that is used as a condition on what voxels to iterate i.e. only voxels
     *     within these bounds
     * @param shiftForSecond added to the current point in {@code voxels1} to give a corresponding
     *     point in {@code voxels2}.
     * @param shiftForThird added to the current point in {@code voxels1} to give a corresponding
     *     point in {@code voxels3}.
     * @param voxels1 voxels in which which {@code box} refers to a subregion.
     * @param voxels2 voxels in which which {@code box + shiftForSecond} refers to a subregion.
     * @param voxels3 voxels in which which {@code box + shiftForThird} refers to a subregion.
     * @param process is called for each voxel within the bounding-box where the point uses
     *     <i>global</i> coordinates without the shift. A new {@link Point3i} is <b>not</b> created
     *     on each iteration.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withThreeBuffers(
            BoundingBox box,
            ReadableTuple3i shiftForSecond,
            ReadableTuple3i shiftForThird,
            Voxels<T> voxels1,
            Voxels<T> voxels2,
            Voxels<T> voxels3,
            ProcessBufferTernary<T> process) {
        ReadableTuple3i max = box.calculateCornerMaxExclusive();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() < max.z(); point.incrementZ()) {

            process.notifyChangeSlice(point.z());

            T buffer1 = voxels1.sliceBuffer(point.z());
            T buffer2 = voxels2.sliceBuffer(point.z() + shiftForSecond.z());
            T buffer3 = voxels3.sliceBuffer(point.z() + shiftForThird.z());

            for (point.setY(box.cornerMin().y()); point.y() < max.y(); point.incrementY()) {
                int ySecond = point.y() + shiftForSecond.y();
                int yThird = point.y() + shiftForThird.y();

                for (point.setX(box.cornerMin().x()); point.x() < max.x(); point.incrementX()) {

                    int offset1 = voxels1.extent().offsetSlice(point);
                    int offset2 = voxels2.extent().offset(point.x() + shiftForSecond.x(), ySecond);
                    int offset3 = voxels3.extent().offset(point.x() + shiftForThird.x(), yThird);

                    process.process(point, buffer1, buffer2, buffer3, offset1, offset2, offset3);
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box using a {@link KernelPointCursor}.
     *
     * @param voxels the voxels to iterator over
     * @param box the box to iterate over (with coordinates referring to {@code voxels}.
     * @param params to use when applying a kernel
     * @param process process is called for each voxel inside the extent using the same coordinates
     *     as the extent.
     */
    public static void withCursor(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params,
            ProcessKernelPointCursor process) {

        KernelPointCursor cursor =
                new KernelPointCursor(
                        voxels.extent().offset(box.cornerMin()),
                        new Point3i(box.cornerMin()),
                        voxels.extent(),
                        voxels.binaryValues().asByte(),
                        params);

        Extent extent = voxels.extent();
        Point3i point = cursor.getPoint();
        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMaxExclusive();
        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            cursor.setIndex(extent.offset(cornerMin.x(), cornerMin.y()));
            process.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {

                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {
                    process.process(cursor);
                    cursor.incrementIndexOnly();
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box using a {@link KernelPointCursor} until a predicate
     * returns true..
     *
     * @param voxels the voxels to iterator over
     * @param box the box to iterate over (with coordinates referring to {@code voxels}.
     * @param params to use when applying a kernel
     * @param predicate evaluates each position of the cursor.
     * @return true as soon as any voxel evaluates true with the {@code predicate}, false if it
     *     never happens.
     */
    public static boolean withCursorUntil(
            BinaryVoxels<UnsignedByteBuffer> voxels,
            BoundingBox box,
            KernelApplicationParameters params,
            PredicateKernelPointCursor predicate) {

        KernelPointCursor cursor =
                new KernelPointCursor(
                        voxels.extent().offset(box.cornerMin()),
                        new Point3i(box.cornerMin()),
                        voxels.extent(),
                        voxels.binaryValues().asByte(),
                        params);

        Extent extent = voxels.extent();
        Point3i point = cursor.getPoint();
        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMaxExclusive();
        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            cursor.setIndex(extent.offset(cornerMin.x(), cornerMin.y()));
            predicate.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {

                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {
                    if (predicate.test(cursor)) {
                        return true;
                    }
                    cursor.incrementIndexOnly();
                }
            }
        }

        return false;
    }
}
