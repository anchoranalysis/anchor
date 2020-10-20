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

import java.util.Optional;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.point.consumer.ScalarThreeDimensionalConsumer;

/**
 * Like {@link IterateVoxelsAll} but specifically for equal-to operations on {@link Voxels} of type
 * {@link UnsignedByteBuffer}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IterateVoxelsEqualTo {

    /**
     * Iterates all points with a specific voxel intensity-value, reusing the {@link Point3i} in
     * each iteration.
     *
     * <p>This means that the same {@link Point3i} instance is passed to the consumer each time and
     * is <b>not</b> newly created for each matching voxel.
     *
     * @param voxels voxels to iterate through
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void equalToReusePoint(
            Voxels<UnsignedByteBuffer> voxels, byte equalToValue, Consumer<Point3i> consumer) {
        IterateVoxelsAll.withBuffer(
                voxels,
                (point, buffer, offset) -> {
                    if (buffer.getRaw(offset) == equalToValue) {
                        consumer.accept(point);
                    }
                });
    }

    /**
     * Iterates through all points with a specific voxel intensity-value, passing coordinates as
     * primitive types.
     *
     * @param voxels voxels to iterate through
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void equalToPrimitive(
            Voxels<UnsignedByteBuffer> voxels,
            byte equalToValue,
            ScalarThreeDimensionalConsumer consumer) {
        Extent extent = voxels.extent();
        extent.iterateOverZ(z -> equalToPrimitiveSlice(voxels, z, equalToValue, consumer));
    }

    /**
     * Like {@link #equalToPrimitive} but only iterates over one specific z-slice.
     *
     * @param voxels voxels to iterate through
     * @param sliceIndex which slice to iterate over (z coordinate)
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void equalToPrimitiveSlice(
            Voxels<UnsignedByteBuffer> voxels,
            int sliceIndex,
            byte equalToValue,
            ScalarThreeDimensionalConsumer consumer) {
        UnsignedByteBuffer buffer = voxels.sliceBuffer(sliceIndex);

        voxels.extent()
                .iterateOverXY(
                        (x, y, offset) -> {
                            if (buffer.getRaw() == equalToValue) {
                                consumer.accept(x, y, sliceIndex);
                            }
                        });
    }

    /**
     * Iterates each voxel until a specific intensity value is found.
     *
     * @param voxels the voxels to iterate over
     * @param equalToValue voxels match if they are equal to this value
     * @return the first point found in global-coordinates (newly created), or empty() if no points
     *     are equal-to.
     */
    public static Optional<Point3i> untilFirstIntensityEqualTo(
            BoundedVoxels<UnsignedByteBuffer> voxels, byte equalToValue) {

        Extent extentMask = voxels.extent();
        ReadableTuple3i corner = voxels.boundingBox().cornerMin();

        for (int z = 0; z < extentMask.z(); z++) {

            UnsignedByteBuffer bufferMask = voxels.sliceBufferLocal(z);

            for (int y = 0; y < extentMask.y(); y++) {

                for (int x = 0; x < extentMask.x(); x++) {

                    if (bufferMask.getRaw() == equalToValue) {
                        return Optional.of(new Point3i(corner.x() + x, corner.y(), corner.z() + z));
                    }
                }
            }
        }
        return Optional.empty();
    }
}
