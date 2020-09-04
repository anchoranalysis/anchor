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
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.arithmetic.RunningSum;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.geometry.consumer.PointThreeDimensionalConsumer;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.convert.PrimitiveConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsByte {

    /**
     * Iterates through all points with a specific voxel-value reusing the {@link Point3i} in each
     * iteration.
     *
     * <p>This means that the same {@link Point3i} instance is passed to the consumer each time and
     * is <b>not</b> newly created for each matching voxel.
     *
     * @param voxels voxels to iterate through
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void iterateEqualValuesReusePoint(
            Voxels<UnsignedByteBuffer> voxels, byte equalToValue, Consumer<Point3i> consumer) {
        IterateVoxels.callEachPoint(
                voxels,
                (point, buffer, offset) -> {
                    if (buffer.get(offset) == equalToValue) {
                        consumer.accept(point);
                    }
                });
    }

    /**
     * Iterates through all points with a specific voxel-value
     *
     * @param voxels voxels to iterate through
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void iterateEqualValues(
            Voxels<UnsignedByteBuffer> voxels, byte equalToValue, PointThreeDimensionalConsumer consumer) {
        Extent extent = voxels.extent();
        extent.iterateOverZ(z -> iterateEqualValuesSlice(voxels, z, equalToValue, consumer));
    }

    /**
     * Iterates through all points on a slice with a specific voxel-value
     *
     * @param voxels voxels to iterate through
     * @param sliceIndex which slice to iterate over (z coordinate)
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void iterateEqualValuesSlice(
            Voxels<UnsignedByteBuffer> voxels,
            int sliceIndex,
            byte equalToValue,
            PointThreeDimensionalConsumer consumer) {
        UnsignedByteBuffer buffer = voxels.sliceBuffer(sliceIndex);

        voxels.extent()
                .iterateOverXY(
                        (x, y, offset) -> {
                            if (buffer.getByte() == equalToValue) {
                                consumer.accept(x, y, sliceIndex);
                            }
                        });
    }

    /**
     * Calls each voxel that is equal to a specific value until a point is found
     *
     * @param voxels the voxels to iterate over
     * @param equalToValue voxels match if they are equal to this value
     * @return the first point found in global-coordinates (newly created), or empty() if no points
     *     are equal-to.
     */
    public static Optional<Point3i> iterateUntilFirstEqual(
            BoundedVoxels<UnsignedByteBuffer> voxels, byte equalToValue) {

        Extent extentMask = voxels.extent();
        ReadableTuple3i corner = voxels.boundingBox().cornerMin();

        for (int z = 0; z < extentMask.z(); z++) {

            UnsignedByteBuffer bufferMask = voxels.sliceBufferLocal(z);

            for (int y = 0; y < extentMask.y(); y++) {

                for (int x = 0; x < extentMask.x(); x++) {

                    if (bufferMask.getByte() == equalToValue) {
                        return Optional.of(new Point3i(corner.x() + x, corner.y(), corner.z() + z));
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Calculates the sum and count across voxels that correspond to ON voxels on a <i>mask</i>
     *
     * <p>The {@code mask} must have equal extent to {@code voxelsIntensity}
     *
     * @param voxelsIntensity the voxels whose intensity we wish to find the mean of (subject to
     *     {@code mask}
     * @param mask only voxels who correspond to an ON voxels in the mask are included
     * @return the running-sum
     */
    public static RunningSum calculateSumAndCount(Voxels<UnsignedByteBuffer> voxelsIntensity, Mask mask) {
        Preconditions.checkArgument(voxelsIntensity.extent().equals(mask.extent()));

        RunningSum running = new RunningSum();

        IterateVoxels.callEachPoint(
                voxelsIntensity,
                mask,
                (point, buffer, offset) -> addFromBufferToRunning(buffer, offset, running));

        return running;
    }

    /**
     * Iterate over each voxel in a bounding-box - applying a binary operation with values from
     * <b>two</b> input associated buffers for each slice and writing it into an output buffer
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
     * @param operation is called for each voxel within the bounding-box using GLOBAL coordinates.
     */
    public static void callEachPointWithBinaryOperation(
            Voxels<UnsignedByteBuffer> voxelsIn1,
            Voxels<UnsignedByteBuffer> voxelsIn2,
            Voxels<UnsignedByteBuffer> voxelsOut,
            IntBinaryOperation operation) {
        Preconditions.checkArgument(voxelsIn1.extent().equals(voxelsIn2.extent()));
        Preconditions.checkArgument(voxelsIn2.extent().equals(voxelsOut.extent()));

        for (int z = 0; z < voxelsOut.extent().z(); z++) {

            UnsignedByteBuffer in1 = voxelsIn1.sliceBuffer(z);
            UnsignedByteBuffer in2 = voxelsIn2.sliceBuffer(z);
            UnsignedByteBuffer out = voxelsOut.sliceBuffer(z);

            while (in1.hasRemaining()) {
                out.putInt( operation.apply(in1.getInt(), in2.getInt()) );
            }

            assert (!in2.hasRemaining());
            assert (!out.hasRemaining());
        }
    }

    /**
     * Calculates the sum and count across voxels that correspond to ON voxels on an
     * <i>object-mask</i>
     *
     * @param voxelsIntensity the voxels whose intensity we wish to find the mean of (subject to
     *     {@code mask}
     * @param object only voxels who correspond to an ON voxels in this object-mask are included
     * @return the running-sum
     */
    public static RunningSum calculateSumAndCount(
            Voxels<UnsignedByteBuffer> voxelsIntensity, ObjectMask object) {

        RunningSum running = new RunningSum();

        IterateVoxels.callEachPoint(
                voxelsIntensity,
                object,
                (point, buffer, offset) -> addFromBufferToRunning(buffer, offset, running));

        return running;
    }

    private static void addFromBufferToRunning(UnsignedByteBuffer buffer, int offset, RunningSum running) {
        int intensity = PrimitiveConverter.unsignedByteToInt(buffer.get(offset));
        running.increment(intensity, 1);
    }
}
