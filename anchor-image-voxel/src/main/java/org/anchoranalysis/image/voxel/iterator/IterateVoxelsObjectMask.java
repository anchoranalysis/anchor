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

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.function.IntPredicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.image.voxel.ExtentMatchHelper;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPoint;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnary;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferBinaryMixed;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferUnary;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Utilities for iterating over the subset of voxels corresponding to an <i>on</i> state in an
 * {@link ObjectMask}.
 *
 * <p>The utilities operate on one or more {@link Voxels}. A processor is called on each selected
 * voxel.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsObjectMask {

    /**
     * Iterate over each voxel that is located on an object-mask
     *
     * @param object the object-mask that is used as a condition on what voxels to iterate
     * @param process process is called for each voxel with that satisfies the conditions using
     *     <i>global</i> coordinates.
     */
    public static void withPoint(ObjectMask object, ProcessPoint process) {
        IterateVoxelsBoundingBox.withPoint(
                object.boundingBox(), new RequireIntersectionWithObject(process, object));
    }

    /**
     * Iterate over each point that is located on an object-mask AND optionally a second-mask
     *
     * <p>If a second object-mask is defined, it is a logical AND condition. A voxel is only
     * processed if it exists in both object-masks.
     *
     * @param firstMask the first-mask that is used as a condition on what voxels to iterate
     * @param secondMask an optional second-mask that can be a further condition
     * @param process is called for each voxel with that satisfies the conditions using GLOBAL
     *     coordinates for each voxel.
     */
    public static void withPoint(
            ObjectMask firstMask, Optional<ObjectMask> secondMask, ProcessPoint process) {
        if (secondMask.isPresent()) {
            Optional<BoundingBox> intersection =
                    firstMask.boundingBox().intersection().with(secondMask.get().boundingBox());
            intersection.ifPresent(
                    box ->
                            IterateVoxelsBoundingBox.withPoint(
                                    box,
                                    requireIntersectionTwice(
                                            firstMask, secondMask.get(), process)));
        } else {
            withPoint(firstMask, process);
        }
    }

    /**
     * Iterate over each voxel in an object-mask - with <b>one</b> associated <b>buffer</b> for each
     * slice from {@link Voxels}.
     *
     * @param object the object-mask is used as a condition on what voxels to iterate i.e. only
     *     voxels within these bounds
     * @param voxels voxels where buffers extracted from be processed, and which define the global
     *     coordinate space
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withBuffer(
            ObjectMask object, Voxels<T> voxels, ProcessBufferUnary<T> process) {
        /**
         * This is re-implemented in full, as reusing existing code with {@link AddOffsets} and /
         * {@link RequireIntersectionWithMask} was not inlining using default JVM settings / Based
         * on unit-tests, it seems to perform better empirically, even with the new Point3i() /
         * adding to the heap.
         */
        Extent extent = voxels.extent();

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        byte valueOn = object.binaryValuesByte().getOn();

        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMaxExclusive();

        Point3i point = new Point3i();

        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            T buffer = voxels.sliceBuffer(point.z());
            UnsignedByteBuffer bufferObject = object.sliceBufferGlobal(point.z());
            process.notifyChangeSlice(point.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {

                int offset = extent.offset(cornerMin.x(), point.y());

                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {

                    if (bufferObject.getRaw() == valueOn) {
                        process.process(point, buffer, offset);
                    }
                    offset++;
                }
            }
        }
    }

    /**
     * Iterate over each voxel with a corresponding <i>on</i> value in an object-mask - and with
     * <b>two</b> associated <b>buffers</b> for each slice covering the <b>all the global space</b>
     * i.e. the entire image.
     *
     * <p>The extent's of both {@code voxels1} and {@code voxels2} must be equal, and equal to the
     * coordinate space {@code object} is defined on.
     *
     * @param object an object-mask which restricts which voxels of {@code voxels1} and {@code
     *     voxels2} are iterated
     * @param voxels1 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>first</b> buffer
     * @param voxels2 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>second</b> buffer
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <S> <i>first</i> buffer-type for voxels.
     * @param <T> <i>second</i> buffer-type for voxels.
     */
    public static <S, T> void withTwoBuffers(
            ObjectMask object,
            Voxels<S> voxels1,
            Voxels<T> voxels2,
            ProcessBufferBinary<S, T> process) {
        Preconditions.checkArgument(voxels1.extent().equals(voxels2.extent()));
        IterateVoxelsObjectMask.withPoint(
                object, new RetrieveBuffersForTwoSlices<>(voxels1, voxels2, process));
    }

    /**
     * Iterate over each voxel on an object-mask with <b>one</b> associated {@link VoxelBuffer}.
     *
     * <p>Optionally, the iteration can be restricted to a sub-region of the object-mask.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to {@code process}
     * @param object the object-mask (global coordinates)
     * @param process processes each point that fulfills the conditions
     */
    public static <T> void withVoxelBuffer(
            ObjectMask object, Voxels<T> voxels, ProcessVoxelBufferUnary<T> process) {
        withVoxelBuffer(object, voxels, Optional.empty(), process);
    }

    /**
     * Iterate over each voxel on an object-mask with <b>one</b> associated {@link VoxelBuffer}.
     *
     * <p>Optionally, the iteration can be restricted to a sub-region of the object-mask.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to {@code process}
     * @param object the object-mask (global coordinates)
     * @param restrictTo optional sub-region of object-mask (global coordinates)
     * @param process processes each point that fulfills the conditions
     */
    public static <T> void withVoxelBuffer(
            ObjectMask object,
            Voxels<T> voxels,
            Optional<BoundingBox> restrictTo,
            ProcessVoxelBufferUnary<T> process) {
        BoundingBox boxVoxels =
                restrictTo.orElseGet(() -> object.boundingBox().clampTo(voxels.extent()));

        Preconditions.checkArgument(voxels.extent().contains(boxVoxels));

        Optional<BoundingBox> restrictToIntersection =
                OptionalUtilities.flatMap(
                        restrictTo, box -> box.intersection().with(object.boundingBox()));

        if (restrictTo.isPresent() && !restrictToIntersection.isPresent()) {
            // There's no intersection between the object-mask and restrictTo, so there's nothing to
            // be done. Exit early
            return;
        }

        // What part of the object-mask is iterated over. If not restricted, all of the mask is
        // iterated over */
        BoundingBox iterateBox =
                restrictToIntersection
                        .map(box -> box.relativePositionToBox(object.boundingBox()))
                        .orElseGet(boxVoxels::shiftToOrigin);

        callEachPoint(object, voxels, boxVoxels, iterateBox, process);
    }

    /**
     * Iterate over each voxel in an object-mask - with <b>two associated voxel-buffers</b> and for
     * each slice.
     *
     * <p>The extent's of both {@code voxels1} and {@code voxels2} must be equal.
     *
     * @param object the object-mask, with coordinates relative to both {@code voxels1} and {@code
     *     voxels2}.
     * @param voxels1 voxels that provide the element from the <i>first</i> voxel-buffer.
     * @param voxels2 voxels that provide the element from the <i>second</i> voxel-buffer.
     * @param process is called for each voxel using <i>global</i> coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <S, T> void withTwoVoxelBuffers(
            ObjectMask object,
            Voxels<S> voxels1,
            Voxels<T> voxels2,
            ProcessVoxelBufferBinary<S, T> process) {
        Preconditions.checkArgument(voxels1.extent().equals(voxels2.extent()));

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMaxInclusive();

        byte maskOn = object.binaryValuesByte().getOn();

        Extent e = voxels1.extent();
        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            VoxelBuffer<S> buffer = voxels1.slice(z);
            VoxelBuffer<T> bufferFinalized = voxels2.slice(z);
            UnsignedByteBuffer bufferMask = object.sliceBufferGlobal(z);

            int offset = 0;
            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    if (bufferMask.getRaw(offset) == maskOn) {
                        process.process(buffer, bufferFinalized, e.offset(x, y));
                    }

                    offset++;
                }
            }
        }
    }

    /**
     * Iterate over each voxel in an object-mask - with <b>one associated voxel-buffer</b> and
     * <b>one associated buffer</b> for each slice.
     *
     * <p>The extent's of both {@code voxels1} and {@code voxels2} must be equal.
     *
     * <p>Note that a new {@link Point3i} is created for each call to {@code process}.
     *
     * @param object the object-mask, with coordinates relative to both {@code voxels1} and {@code
     *     voxels2}.
     * @param voxels1 voxels that provide the <b>first</b> element, the voxel-buffer.
     * @param voxels2 voxels that provide the <b>second</b> element, the buffer.
     * @param process is called for each voxel using <i>global</i> coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <S, T> void withTwoMixedBuffers(
            ObjectMask object,
            Voxels<S> voxels1,
            Voxels<T> voxels2,
            ProcessVoxelBufferBinaryMixed<S, T> process) {
        Preconditions.checkArgument(voxels1.extent().equals(voxels2.extent()));

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMaxInclusive();

        byte maskOn = object.binaryValuesByte().getOn();

        Extent e = voxels1.extent();
        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            VoxelBuffer<S> buffer = voxels1.slice(z);
            T bufferFinalized = voxels2.sliceBuffer(z);
            UnsignedByteBuffer bufferMask = object.sliceBufferGlobal(z);

            int offset = 0;
            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    if (bufferMask.getRaw(offset) == maskOn) {
                        process.process(
                                new Point3i(x, y, z), buffer, bufferFinalized, e.offset(x, y));
                    }

                    offset++;
                }
            }
        }
    }

    /**
     * Do all points on an object-mask match a predicate on the point's voxel-intensity?
     *
     * <p>The voxel-value of the current buffer is passed to the predicate.
     *
     * <p>As soon as one voxel fails the predicate (i.e. the predicate returns false), the function
     * immediately returns false.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to the predicate
     * @param object the object-mask (global coordinates) to restrict which voxels are tested with
     *     the predicate
     * @param predicate a predicate applied on the intensity of each voxel
     * @return true if the predicate returns true for all points on the object-mask, false otherwise
     */
    public static <T> boolean allMatchIntensity(
            ObjectMask object, Voxels<T> voxels, IntPredicate predicate) {

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMaxExclusive();

        byte maskMatchValue = object.binaryValuesByte().getOn();

        Extent extentVoxels = voxels.extent();

        Point3i point = new Point3i();
        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            VoxelBuffer<T> buffer = voxels.slice(point.z());
            UnsignedByteBuffer sliceMask = object.sliceBufferGlobal(point.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {
                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {

                    if (sliceMask.getRaw() == maskMatchValue) {

                        int offset = extentVoxels.offsetSlice(point);

                        int voxelIntensity = buffer.getInt(offset);

                        if (!predicate.test(voxelIntensity)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Calls each point on a bounding-box subregion of an object-mask
     *
     * <p>{@code boxVoxels} and {@code boxRelativeToObject} must both have the same extent.
     *
     * @param <T> buffer-type
     * @param voxels voxels which provide a buffer passed to {@code process}
     * @param boxVoxels bounding-box in {@code voxels} to iterate over
     * @param object the object-mask (global coordinates)
     * @param boxRelativeToObject bounding-box expressed <i>relative</i> to the object-mask's
     *     bounding-box
     * @param process processes each point that fulfills the conditions
     */
    private static <T> void callEachPoint(
            ObjectMask object,
            Voxels<T> voxels,
            BoundingBox boxVoxels,
            BoundingBox boxRelativeToObject,
            ProcessVoxelBufferUnary<T> process) {

        assert (voxels.extent().contains(boxVoxels));

        ExtentMatchHelper.checkExtentMatch(boxVoxels, boxRelativeToObject);

        ReadableTuple3i cornerMin = boxVoxels.cornerMin();
        ReadableTuple3i cornerMax = boxVoxels.calculateCornerMaxExclusive();

        // Adding this to the voxels (global) coordinate gives a local coordinate for the
        // object-mask
        ReadableTuple3i maskShift =
                Point3i.immutableSubtract(boxRelativeToObject.cornerMin(), cornerMin);

        byte maskMatchValue = object.binaryValuesByte().getOn();

        Extent extentVoxels = voxels.extent();
        Extent extentObject = object.extent();

        Point3i point = new Point3i();
        for (point.setZ(cornerMin.z()); point.z() < cornerMax.z(); point.incrementZ()) {

            VoxelBuffer<T> buffer = voxels.slice(point.z());
            UnsignedByteBuffer sliceMask = object.sliceBufferLocal(point.z() + maskShift.z());

            for (point.setY(cornerMin.y()); point.y() < cornerMax.y(); point.incrementY()) {
                for (point.setX(cornerMin.x()); point.x() < cornerMax.x(); point.incrementX()) {

                    int indexMask =
                            extentObject.offset(
                                    point.x() + maskShift.x(), point.y() + maskShift.y());

                    if (sliceMask.getRaw(indexMask) == maskMatchValue) {

                        int offset = extentVoxels.offsetSlice(point);
                        process.process(buffer, offset);
                    }
                }
            }
        }
    }

    private static ProcessPoint requireIntersectionTwice(
            ObjectMask object1, ObjectMask object2, ProcessPoint processor) {
        ProcessPoint inner = new RequireIntersectionWithObject(processor, object2);
        return new RequireIntersectionWithObject(inner, object1);
    }
}
