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
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.IntBinaryOperation;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.convert.UnsignedBufferAsInt;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.process.ProcessPoint;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinaryWithPoint;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferTernaryWithPoint;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferUnaryWithPoint;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferUnary;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferUnaryWithPoint;

/**
 * Utilities for iterating over <i>all</i> voxels in one or more {@link Voxels}.
 *
 * <p>A processor is called on each voxel.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsAll {

    /**
     * Iterate over each voxel in an {@link Extent}
     *
     * @param extent the extent to be iterated over
     * @param process process is called for each voxel inside the extent using the same coordinates
     *     as the extent.
     */
    public static void withPoint(Extent extent, ProcessPoint process) {
        IterateVoxelsBoundingBox.withPoint(new BoundingBox(extent), process);
    }

    /**
     * Iterate over each voxel - with <b>one</b> associated <b>buffer</b> for each slice.
     *
     * @param voxels voxels to be iterated over (in their entirety)
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withBuffer(Voxels<T> voxels, ProcessBufferUnaryWithPoint<T> process) {
        withPoint(voxels.extent(), new RetrieveBufferForSlice<>(voxels, process));
    }

    /**
     * Iterate over each voxel - with <b>two</b> associated <b>buffers</b> for each slice
     *
     * <p>The extent's of both {@code voxels1} and {@code voxels2} must be equal.
     *
     * @param voxels1 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>first</b> buffer
     * @param voxels2 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>second</b> buffer
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withTwoBuffersAndPoint(
            Voxels<T> voxels1, Voxels<T> voxels2, ProcessBufferBinaryWithPoint<T> process) {
        Preconditions.checkArgument(voxels1.extent().equals(voxels2.extent()));
        withPoint(voxels1.extent(), new RetrieveBuffersForTwoSlices<>(voxels1, voxels2, process));
    }

    /**
     * Iterate over each voxel - with <b>three</b> associated <b>buffers</b> for each slice.
     *
     * <p>The extent's of both {@code voxels1} and {@code voxels2} and {@code voxels3} must be
     * equal.
     *
     * @param voxels1 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>first</b> buffer
     * @param voxels2 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>second</b> buffer
     * @param voxels3 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>third</b> buffer
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withThreeBuffers(
            Voxels<T> voxels1,
            Voxels<T> voxels2,
            Voxels<T> voxels3,
            ProcessBufferTernaryWithPoint<T> process) {
        Preconditions.checkArgument(voxels1.extent().equals(voxels2.extent()));
        Preconditions.checkArgument(voxels2.extent().equals(voxels3.extent()));
        withPoint(
                voxels1.extent(),
                new RetrieveBuffersForThreeSlices<>(voxels1, voxels2, voxels3, process));
    }

    /**
     * Iterate over each voxel - with <b>one</b> associated <b>voxel-buffer</b> for each slice.
     *
     * <p>It is similar to {@link #withVoxelBuffer(Voxels, ProcessVoxelBufferUnaryWithPoint)} but a
     * {@link Point3i} is <i>not</i> exposed.
     *
     * @param voxels voxels to be iterated over (in their entirety)
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <T> void withVoxelBuffer(Voxels<T> voxels, ProcessVoxelBufferUnary<T> process) {

        int volumeXY = voxels.extent().volumeXY();

        voxels.extent()
                .iterateOverZ(
                        z -> {
                            VoxelBuffer<T> buffer = voxels.slice(z);

                            for (int offset = 0; offset < volumeXY; offset++) {
                                process.process(buffer, offset);
                            }
                        });
    }

    /**
     * Iterate over each voxel - with <b>one</b> associated <b>voxel-buffer</b> for each slice.
     *
     * <p>It is similar to {@link #withVoxelBuffer(Voxels, ProcessVoxelBufferUnary)} but a {@link
     * Point3i} is also exposed.
     *
     * @param voxels voxels to be iterated over (in their entirety)
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     * @throws E exception that may be thrown by the processor
     */
    public static <T, E extends Exception> void withVoxelBuffer(
            Voxels<T> voxels, ProcessVoxelBufferUnaryWithPoint<T, E> process) throws E {

        Extent extentVoxels = voxels.extent();

        Point3i point = new Point3i();
        for (point.setZ(0); point.z() < extentVoxels.z(); point.incrementZ()) {

            VoxelBuffer<T> buffer = voxels.slice(point.z());

            int offset = 0;
            for (point.setY(0); point.y() < extentVoxels.y(); point.incrementY()) {
                for (point.setX(0); point.x() < extentVoxels.x(); point.incrementX()) {
                    process.process(point, buffer, offset++);
                }
            }
        }
    }

    /**
     * Iterate over each voxel in a bounding-box - with <b>two</b> associated <b>voxel-buffers</b>
     * for each slice
     *
     * <p>The extent's of both {@code voxels1} and {@code voxels2} must be equal.
     *
     * @param voxels1 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>first</b> buffer
     * @param voxels2 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>second</b> buffer
     * @param process is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     * @param <T> buffer-type for voxels
     */
    public static <S, T> void withTwoVoxelBuffers(
            Voxels<S> voxels1, Voxels<T> voxels2, ProcessVoxelBufferBinary<S, T> process) {
        Preconditions.checkArgument(voxels1.extent().equals(voxels2.extent()));

        int volumeXY = voxels1.extent().volumeXY();

        voxels1.extent()
                .iterateOverZ(
                        z -> {
                            VoxelBuffer<S> buffer1 = voxels1.slice(z);
                            VoxelBuffer<T> buffer2 = voxels2.slice(z);

                            for (int offset = 0; offset < volumeXY; offset++) {
                                process.process(buffer1, buffer2, offset);
                            }
                        });
    }

    /**
     * Changes each voxel, reading and writing the buffer as an {@code int}.
     *
     * <p>Note this provides slower access than operating on the native-types.
     *
     * @param voxels the voxels, each of which is transformed by {@code operator}
     * @param operator determines a corresponding <i>output</i> value for each <i>input</i> voxel
     */
    public static void changeIntensity(Voxels<?> voxels, IntUnaryOperator operator) {
        voxels.slices()
                .iterateOverSlicesAndOffsets(
                        (buffer, offset) -> {
                            int value = buffer.getInt(offset);
                            buffer.putInt(offset, operator.applyAsInt(value));
                        });
    }

    /**
     * Finds the maximum intensity-value (as an int) among all voxels.
     *
     * <p>Note this provides slower access than operating on the native-types.
     *
     * @param <T> the buffer-type
     * @param voxels the voxels
     * @return whatever the maximum value is
     */
    public static <T extends UnsignedBufferAsInt> int intensityMax(Voxels<T> voxels) {
        int max = 0;
        boolean first = true;

        int sizeXY = voxels.extent().volumeXY();
        for (int z = 0; z < voxels.extent().z(); z++) {

            T buffer = voxels.sliceBuffer(z);

            for (int offset = 0; offset < sizeXY; offset++) {

                int val = buffer.getUnsigned(offset);
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return max;
    }

    /**
     * Assigns a value to any voxel whose intensity matches a predicate, reading and writing the
     * buffer as an {@code int}.
     *
     * <p>Note this provides slower access than operating on the native-types.
     *
     * @param voxels the voxels, each of which is tested by {@code predicate} and maybe assigned a
     *     new value
     * @param predicate determines if a voxel-value should be assigned or not
     * @param valueToAssign the value to assign
     */
    public static void assignEachMatchingPoint(
            Voxels<?> voxels, IntPredicate predicate, int valueToAssign) {

        voxels.slices()
                .iterateOverSlicesAndOffsets(
                        (buffer, offset) -> {
                            if (predicate.test(buffer.getInt(offset))) {
                                buffer.putInt(offset, valueToAssign);
                            }
                        });
    }

    /**
     * Iterate over each voxel in a bounding-box - applying a binary operation with values from
     * <b>two</b> input {@code Voxels<UnsignedByteBuffer>} for each slice and writing it into an
     * output {@code Voxels<UnsignedByteBuffer>}.
     *
     * <p>The extent's of both {@code voxelsIn1} and {@code voxelsIn2} and {@code voxelsOut} must be
     * equal.
     *
     * @param voxelsIn1 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>first inwards</b> buffer
     * @param voxelsIn2 voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>second inwards</b> buffer
     * @param voxelsOut voxels in which which {@link BoundingBox} refers to a subregion, and which
     *     provides the <b>outwards</b> buffer
     * @param operation is called for each voxel within the bounding-box using <i>global</i>
     *     coordinates.
     */
    public static void binaryOperation(
            Voxels<UnsignedByteBuffer> voxelsIn1,
            Voxels<UnsignedByteBuffer> voxelsIn2,
            Voxels<UnsignedByteBuffer> voxelsOut,
            IntBinaryOperation operation) {
        Preconditions.checkArgument(voxelsIn1.extent().equals(voxelsIn2.extent()));
        Preconditions.checkArgument(voxelsIn2.extent().equals(voxelsOut.extent()));

        voxelsOut
                .extent()
                .iterateOverZ(
                        z -> {
                            UnsignedByteBuffer in1 = voxelsIn1.sliceBuffer(z);
                            UnsignedByteBuffer in2 = voxelsIn2.sliceBuffer(z);
                            UnsignedByteBuffer out = voxelsOut.sliceBuffer(z);

                            while (in1.hasRemaining()) {
                                out.putUnsigned(
                                        operation.apply(in1.getUnsigned(), in2.getUnsigned()));
                            }

                            assert (!in2.hasRemaining());
                            assert (!out.hasRemaining());
                        });
    }
}
